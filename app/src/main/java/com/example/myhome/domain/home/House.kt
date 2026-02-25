package com.example.myhome.domain.home

data class House(
    val id: String? = null,
    val name: String? = null,
    val address: String? = null,
    val totalRoom:Int,
    val totalDevice:Int,
    val totalPower:Int,
    val totalSensor:Int,
)
