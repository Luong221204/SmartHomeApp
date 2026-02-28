package com.example.myhome.domain.sensor

import com.example.myhome.domain.device.TimeDto
import com.google.android.datatransport.cct.StringMerger
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class Sensor(
    val createdAt: TimeDto?= null,
    val current: Map<String, Float>?= null,
    val id:String?= null,
    val name:String?= null,
    val refferTo:String?= null,
    val houseId: String?= null,
    val roomId:String?= null,
    val status:Boolean?= null,
    val type: SensorType?= null,
    val lastUpdated: TimeDto?= null,
    val data: Map<String,List<Data>>?= null,
    var isSelected:Boolean=false
)

data class SensorType(
    val image:String,
    val max:Float,
    val min:Float,
    val id:String,
    val unit:Map<String, String>,
    val type:String,
    val threshold: Map<String,SeverityLevel>
)
data class SeverityLevel(
    val value:String,
    val color: String
){
    var min : String = value.split("-")[0]
    var max : String = value.split("-")[1]
}
data class Data(
    val level: Float,
    val time: TimeDto
){
    fun convertTime():String{
        val instant = Instant.ofEpochSecond(time._seconds)
        val dateTime = LocalDateTime.ofInstant(
            instant,
            ZoneId.systemDefault()
        )
        return dateTime.format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        )
    }
    fun convertTimeDay():String{
        val instant = Instant.ofEpochSecond(time._seconds)
        val dateTime = LocalDateTime.ofInstant(
            instant,
            ZoneId.systemDefault()
        )
        return dateTime.format(
            DateTimeFormatter.ofPattern("HH:mm")
        )
    }
}