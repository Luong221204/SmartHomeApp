package com.example.myhome.domain.device

import java.io.Serializable

data class Device(
    val id :String?= null,
    val name:String?= null,
    val houseId:String?=null,
    val roomId:String?= null,
    val levels: Map<String, Float>?= null,
    val kwh:Int?= null,
    val status:Boolean?= null,
    val type:String?= null,
    val value: Float?= null,
    val createdAt: TimeDto?= null,
) {
}

data class TimeDto(
    val _seconds: Long,
    val _nanoseconds: Int
){
    fun getMilliSecond():Long{
        return _seconds*1000+_nanoseconds/1000000
    }
}

data class EnergyStat(
    val lastUpdated: TimeDto,
    val kwh: Int,
    val date: String,
    val lastWatt:Int
)

data class ActivityLog(
    val id:String,
    val description:String,
    val time:TimeDto,
    val type:String,
    val value:Int
)