package com.example.myhome.domain.home

import com.example.myhome.domain.device.TimeDto
import kotlinx.serialization.Serializable


@Serializable
data class Room(
    val houseId: String?=null,
    val totalDevice: Int?=null,
    val id:String?=null,
    val name: String,
    val type:String?=null,
    val createAt: TimeDto?=null
)
