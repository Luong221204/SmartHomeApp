package com.example.myhome.domain

import com.example.myhome.domain.enum.RefreshTokenCode

data class User(
    val id:String = "",
    val name: String = "",
    val email:String = "",
    val password:String = "",
    val confirmPassword:String = "",
    val access_token:String = "",
    val newPassword:String = "",
    val otp : String = "",
    val message :String = "",
    val status:Boolean = false,
    val refreshToken:String = "",
    val code: String = "",
    val houseIds : List<String>? = emptyList(),
    val fcmTokens: List<String>? = emptyList()
)
