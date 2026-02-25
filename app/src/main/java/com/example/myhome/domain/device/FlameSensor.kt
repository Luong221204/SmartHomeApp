package com.example.myhome.domain.device

import com.example.myhome.domain.sensor.Data
import java.io.Serializable

data class FlameSensor(
    val status :Boolean,
    val data :List<Data> = emptyList(),
    val infor:String = "",
    val level :Int = 0
): Serializable