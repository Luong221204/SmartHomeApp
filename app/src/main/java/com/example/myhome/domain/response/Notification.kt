package com.example.myhome.domain.response

import com.example.myhome.domain.device.TimeDto

data class Notification(
    val type : String?=null,
    val title : String?=null,
    val body : String?=null,
    val address : String?=null,

    val description : String?=null,
    val createdAt : TimeDto?=null,
    val houseId :String?=null
)