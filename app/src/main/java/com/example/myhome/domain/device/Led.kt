package com.example.myhome.domain.device

import java.io.Serializable

data class Led(
    val status: Boolean = false,
    val location : String = ""
): Serializable
