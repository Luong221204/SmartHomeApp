package com.example.myhome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.domain.device.Password
import com.example.myhome.domain.response.Result
import com.example.myhome.network.ApiConnect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class PasswordViewmodel : ViewModel() {

    var _password = MutableStateFlow<Result>(Result.Loading)
    val password : StateFlow<Result> = _password
    init {
       getPassword()
    }
    fun getPassword(){
        viewModelScope.launch {
            _password.emit(Result.Loading)
            try {
                val result = ApiConnect.service?.getPassword()
                val r = Result.Response<Response<Password>>(result)
                _password.emit(r)
            }catch (e: Exception){
                _password.emit(Result.Error())
            }
        }
    }
    suspend fun changePassword(p:String):Boolean{
        return try {
            val result = ApiConnect.service?.changePassword(Password(p)) // suspend call
            getPassword()
            result?.body()?.success == true
        } catch (e: Exception) {
            false
        }
    }
}