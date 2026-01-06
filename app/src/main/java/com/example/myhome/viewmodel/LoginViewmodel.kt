package com.example.myhome.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.domain.User
import com.example.myhome.domain.response.Result
import com.example.myhome.local.DataManager
import com.example.myhome.network.ApiConnect
import com.example.myhome.network.FcmToken
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class LoginViewmodel : ViewModel() {
    var _login = MutableSharedFlow<Result>()
    val login : SharedFlow<Result> = _login

    var _forgot = MutableSharedFlow<Result>()
    val forgot : SharedFlow<Result> = _forgot

    var _reset = MutableSharedFlow<Result>()
    val reset : SharedFlow<Result> = _reset

    fun login(name:String,email: String, password: String){
        viewModelScope.launch {
            _login.emit(Result.Loading)
            try {
                val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
                    throwable.printStackTrace()
                    viewModelScope.launch {
                        _login.emit(Result.Error("Lỗi hệ thống"))
                    }
                }
                val scope = CoroutineScope(SupervisorJob()+ Dispatchers.Default+coroutineExceptionHandler)
                val result = ApiConnect.service!!.login(User(email = email.trim(), password = password.trim()))
                val res = Result.Response<Response<User>>(result)
                DataManager.saveLoginStatus(true)
                DataManager.saveUser(result.body()!!)
                Log.d("FCM", " ${DataManager.getUser().houseIds?.size}")

                res.t?.body()?.apply {
                    if(this.status == false) _login.emit(Result.Error(this.message))
                    else {
                        FirebaseMessaging.getInstance().token
                            .addOnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    runBlocking {
                                        Log.e("FCM", "Lấy token thất bại", task.exception)
                                        _login.emit(Result.Error("Lỗi hệ thống"))
                                    }
                                    return@addOnCompleteListener
                                }
                                scope.launch {
                                    val token = task.result
                                    Log.d("FCM", "FCM Token: $token")
                                    DataManager.saveFcmToken(token)
                                    val r = ApiConnect.service!!.updateFcmToken(FcmToken(token,
                                        DataManager.getUser().id))
                                    if(r.isSuccessful){
                                        _login.emit(res)
                                    }else{
                                        _login.emit(Result.Error("Lỗi hệ thống"))
                                    }
                                    // 👉 Gửi token lên server NestJS
                                }.invokeOnCompletion {
                                    if(it == null) {
                                        Log.d("FCM", "Gửi token thành công")
                                    }
                                }
                            }
                    }
                }
            }catch (e: Exception){
                _login.emit(Result.Error("Lỗi hệ thống"))
            }
        }
    }
    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _forgot.emit(Result.Loading)
             try {
                val result = ApiConnect.service!!.forgot(User(email = email.trim()))
                val res = Result.Response<Response<User>>(result)

                 res.t?.body()?.apply {
                     if(this.status == false) _forgot.emit(Result.Error(this.message))
                     else _forgot.emit(res)
                 }

            } catch (e: Exception) {
                _forgot.emit(Result.Error("Lỗi hệ thống"))

            }
        }

    }
    fun resetPassword(email:String,otp: String, password: String) {
        viewModelScope.launch {
            _reset.emit(Result.Loading)
            try {
                val result = ApiConnect.service!!.resetAfterForgot(User(email = email.trim(),otp = otp.trim(), newPassword = password.trim()))
                val res = Result.Response<Response<User>>(result)

                res.t?.body()?.apply {
                    if(this.status == false) _reset.emit(Result.Error(this.message))
                    else _reset.emit(res)
                }

            } catch (e: Exception) {
                _reset.emit(Result.Error("Lỗi hệ thống"))

            }
        }
    }

}