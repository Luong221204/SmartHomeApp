package com.example.myhome.domain.device

import com.example.myhome.domain.sensor.Data

data class RainSensor(
    val status : Boolean,
    val data :List<Data> = emptyList(),
    val infor:String = "",
    val level :Int = 0): java.io.Serializable
