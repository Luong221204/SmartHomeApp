package com.example.myhome.domain.response

data class Notification(
    val type : String,
    val title : String,
    val body : String,
    val address : String,
    val description : String
)