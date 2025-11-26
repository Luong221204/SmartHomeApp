package com.example.myhome.domain.response

import java.io.Serializable

data class Model(
    val success :Boolean =false,
    val error: String?
): Serializable