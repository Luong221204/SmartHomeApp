package com.example.myhome.domain.home

import com.example.myhome.domain.device.TimeDto

data class Room(
    val houseId: String,
    val totalDevice: Int,
    val id:String?=null,
    val name: String,
    val type:String?=null,
    val createAt: TimeDto?=null
)
