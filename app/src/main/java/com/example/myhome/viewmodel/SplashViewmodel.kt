package com.example.myhome.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.domain.User
import com.example.myhome.domain.response.Model
import com.example.myhome.domain.response.Result
import com.example.myhome.network.ApiConnect
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class SplashViewmodel : ViewModel() {
    private val _response = MutableSharedFlow<Result>()
    val response = _response
    init{
        viewModelScope.launch {
            _response.emit(Result.Loading)
            try{
                val r = ApiConnect.service?.start()
                val result = Result.Response<Response<User>>(r)
                if(result.t?.body()?.email != null) {
                    _response.emit(result)
                }else {
                    _response.emit(Result.Error("No user found"))
                }
            }catch (e: Exception){
                _response.emit(Result.Error())
            }
        }
    }
}