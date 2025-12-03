package com.example.myhome.domain

import kotlinx.serialization.Serializable

data class RainSensor(
    val status : Boolean  ,
    val data :List<Data> = emptyList(),
    val infor:String = "",
    val level :Int = 0): java.io.Serializable
