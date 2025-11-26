package com.example.myhome.domain.response

import org.checkerframework.checker.units.qual.Temperature
import java.io.Serializable

data class TempAndHumid(
    val temperature: Double  = 0.0,
    val humidity:Double = 0.0
): Serializable