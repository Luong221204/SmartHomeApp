package com.example.myhome.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                val result = Result.Response<Response<Model>>(r)
                Log.d("DUCLUONG", "start: ${result.t?.body()?.success.toString()}")
                _response.emit(result)
            }catch (e: Exception){
                _response.emit(Result.Error())
            }
        }
    }
}