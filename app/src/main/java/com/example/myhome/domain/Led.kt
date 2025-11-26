package com.example.myhome.domain

import java.io.Serializable

data class Led(
    val status: Boolean = false,
    val location : String = ""
): Serializable
