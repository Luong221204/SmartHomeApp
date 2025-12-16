package com.example.myhome.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.domain.User
import com.example.myhome.domain.response.Result
import com.example.myhome.domain.response.ResultFromHandle
import com.example.myhome.network.ApiConnect
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class RegisterViewmodel : ViewModel() {
    var _register = MutableSharedFlow<Result>()
    val register: SharedFlow<Result> = _register

    fun register(fullName: String, email: String, password: String,confirmPass: String) {
        viewModelScope.launch {
            _register  .emit(Result.Loading)
            try {
                val result = ApiConnect.service!!.register(
                    User(
                        name = fullName.trim(),
                        email = email.trim(),
                        password = password.trim(),
                        confirmPassword = confirmPass.trim()
                    )
                )
                val res = Result.Response<Response<User>>(result)
                res.t?.body()?.apply {
                    if(this.status == false) _register.emit(Result.Error(this.message))
                    else _register.emit(res)
                }
            } catch (e: Exception) {
                _register.emit(Result.Error("Lỗi hệ thống"))
            }
        }
    }


}

