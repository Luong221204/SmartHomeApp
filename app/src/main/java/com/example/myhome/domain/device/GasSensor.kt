package com.example.myhome.domain.device

import androidx.compose.ui.graphics.Color
import com.example.myhome.domain.sensor.Data
import java.io.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class GasSensor(
    val status : Boolean,
    val data :List<Data> = emptyList(),
    val infor:String = "",
    val level :Int = 0
): Serializable


data class SafetyLevel(
    val max :Float,
    val min :Float,
    val color : Color,
    val meaning:String
)