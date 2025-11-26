package com.example.myhome

sealed class Result {
    object Nothing: Result()
    object Loading: Result()
    data class Response<T>(val t :T) :Result()
    object Error:Result()
}