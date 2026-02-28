package com.example.myhome.util

import androidx.compose.ui.graphics.Color
import com.example.myhome.R
import com.example.myhome.domain.device.SafetyLevel

object Constants {
    val list = listOf("cửa", "đèn phòng ngủ", "đèn phòng khách" ,
        "máy bơm","quạt","cảm biến lửa","cảm biến khói","cảm biến mưa","còi")

    val deviceList = mapOf<String, Int>(
        "DHT11" to R.drawable.temperature,
        "MQ2" to R.drawable.gassen,
        "RS" to R.drawable.rain,
        "FS" to R.drawable.flamesen,
        "BUZZ" to R.drawable.buzzer,
        "FAN" to R.drawable.fan,
        "LIGHT" to R.drawable.bulb,
        "DOOR" to R.drawable.closed,
        "PUMP" to R.drawable.pump,
    )
    val unitList = mapOf<String, Int>(
        "temperature" to R.drawable.temperature,
        "humidity" to R.drawable.humidity,
        "analog" to R.drawable.analog,
    )
    val autoList = mapOf<String, Int>(
        "SCHEDULE" to R.drawable.timer,
        "AUTO" to R.drawable.auto,
    )
    val safetyList = listOf(

        SafetyLevel(
            min = 0f,
            max = 1000f,
            color = Color.Companion.Green, // Green
            meaning = "SAFE"
        ),

        SafetyLevel(
            min = 1000f,
            max = 2500f,
            color = Color.Companion.Yellow, // Yellow
            meaning = "WARNING"
        ),

        SafetyLevel(
            min = 2500f,
            max = 3500f,
            color = Color.Companion.Cyan, // Orange
            meaning = "DANGER"
        ),

        SafetyLevel(
            min = 3500f,
            max = 4095f,
            color = Color.Companion.Red, // Red
            meaning = "CRITICAL"
        )
    )
    val chartColors = listOf(
        Color(0xFF4CAF50), // Xanh lá
        Color(0xFF2196F3), // Xanh dương
        Color(0xFFFF9800), // Cam
        Color(0xFFE91E63), // Hồng đậm
        Color(0xFF9C27B0)  // Tím
    )

    val color = mapOf(
        "temperature" to Color(0xFF4CAF50),
        "humidity" to Color(0xFF2196F3),
        "analog" to Color(0xFFFF9800),
    )


    val colorForLog = mapOf(
        "AUTO" to Color(0xFF4CAF50),
        "MANUAL" to Color(0xFF2196F3),
        "SYSTEM" to Color(0xFFF44336)
    )


}