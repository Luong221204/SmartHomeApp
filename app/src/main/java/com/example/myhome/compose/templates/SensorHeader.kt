package com.example.myhome.compose.templates

import com.example.myhome.domain.device.TimeDto
import com.example.myhome.domain.sensor.Data
import com.example.myhome.domain.sensor.SafetyLevel


data class Sensor(
    val id:String?=null,
    val name:String?=null,
    val icon: String?=null,
    val status: Boolean?=null,
    val threshold: SensorThreshold?=null,
    val sensorData:List<SensorData>?=null,
    val data:Map<String,List<Data>>?=null,
    val safetyLevels:List<SafetyLevel>?=null,


    )

data class SensorThreshold(
    val min: Float,
    val max: Float,
    val current: Float
)