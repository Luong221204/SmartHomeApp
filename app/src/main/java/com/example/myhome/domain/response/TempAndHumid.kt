package com.example.myhome.domain.response

import java.io.Serializable

data class TempAndHumid(
    val temperature: Double  = 0.0,
    val humidity:Double = 0.0,
    val rain :Boolean = false,
): Serializable