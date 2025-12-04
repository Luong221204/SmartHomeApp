package com.example.myhome.domain.device

import java.io.Serializable

data class GasSensor(
    val status : Boolean  ,
    val data :List<Data> = emptyList(),
    val infor:String = "",
    val level :Int = 0
): Serializable
data class Data(
    val level: Float,
    val time: String
)