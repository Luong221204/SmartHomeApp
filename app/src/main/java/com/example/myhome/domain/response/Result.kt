package com.example.myhome.domain.response

sealed class Result {
    object Nothing : Result()
    object Loading : Result()
    data class Response<T>(var t: T?) : Result()
    data class Error(val message: String? = null) : Result()
}