package com.example.myhome.domain

data class User(
    val name:String? = null,
    val email:String? = null,
    val password:String? = null,
    val confirmPassword:String? = null,
    val access_token:String? = null,
    val newPassword:String? = null,
    val otp : String? = null,
    val message :String? = null,
    val status:Boolean? = null
)
