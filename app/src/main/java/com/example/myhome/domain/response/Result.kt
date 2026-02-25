package com.example.myhome.domain.response

sealed class Result {
    object Nothing : Result()
    object Loading : Result()
    data class Response<T>(var t: T?) : Result()
    data class Error(val message: String? = null) : Result()
}

sealed class NetworkResult<out T> {
    object Idle : NetworkResult<Nothing>() // Đổi tên Nothing thành Idle để tránh trùng tên với kiểu Nothing của hệ thống
    object Loading : NetworkResult<Nothing>()
    data class Success<out T>(val data: T) : NetworkResult<T>() // Dùng val và out
    data class Error(val message: String? = null) : NetworkResult<Nothing>()
}