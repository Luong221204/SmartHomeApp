package com.example.myhome.domain.device

import com.example.myhome.domain.sensor.Data

data class Temperature(
    val data: Map<String, List<Data>>,
)

