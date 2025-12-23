package com.example.myhome.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.domain.User
import com.example.myhome.domain.response.Result
import com.example.myhome.local.DataManager
import com.example.myhome.network.ApiConnect
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

                val result = ApiConnect.service!!.login(User(email = email.trim(), password = password.trim()))
                val res = Result.Response<Response<User>>(result)
                ApiConnect.setToken(result.body()?.access_token.toString())
                DataManager.saveToken(result.body()?.access_token.toString())
                res.t?.body()?.apply {
                    if(this.status == false) _login.emit(Result.Error(this.message))
                    else _login.emit(res)
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