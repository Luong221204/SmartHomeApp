package com.example.myhome.domain.response

data class ResultFromHandle(
    val isLoading:Boolean,
    val isSuccess:Boolean,
    val message:String,
    val isError:Boolean,
)
