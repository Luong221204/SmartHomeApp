package com.example.myhome.domain.automation

import com.example.myhome.domain.device.TimeDto

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
    val schedule: Schedule?=null
) {
}

data class Action(
    val command: String?=null,
    val value: Int?=null,
    val deviceId: String?=null,
    val status: Boolean?=null,//trạng thái bật tắt
)
data class Condition(
    val operation: String?=null,
    val property:String?=null,
    val sensorId:String?=null,
    val threshold:Int?=null,
)
data class Control(
    val coolDownMinutes:Int?=null,
    val lastExecuted: TimeDto?=null
)
data class Schedule(
    val cron:String?=null,
    val timezone:String?=null
)
