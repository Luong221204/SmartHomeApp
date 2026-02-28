package com.example.myhome.domain.automation

import com.example.myhome.domain.device.TimeDto
import kotlinx.serialization.Serializable

@Serializable
data class Automation(
    val id: String?=null,
    val name: String?=null,
    val houseId: String?=null,
    val roomId: String?=null,
    val isEnabled: Boolean?=null,
    val type:String?=null,
    val lastUpdated: TimeDto?=null,
    val action:Action?=null,
    val condition: Condition?=null,
    val control: Control?=null,
    val schedule: Schedule?=null,
    val createdAt:TimeDto?=null
) {
}

@Serializable
data class Action(
    val command: String?=null,
    val value: Int?=null,
    val deviceId: String?=null,
    val status: Boolean?=null,//trạng thái bật tắt
)

@Serializable
data class Condition(
    val operation: String?=null,
    val property:String?=null,
    val sensorId:String?=null,
    val threshold:Int?=null,
)

@Serializable
data class Control(
    val coolDownMinutes:Int?=null,
    val lastExecuted: TimeDto?=null
)

@Serializable
data class Schedule(
    val cron:String?=null,
    val timezone:String?=null
)

data class Date(
    val hour:Int?=null,
    val minute:Int?=null,
    val second:Int?=null,
    val day:Int?=null,
    val month:Int?=null,
    val year:Int?=null
)
