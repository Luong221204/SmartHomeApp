package com.example.myhome.domain.device

import androidx.compose.ui.graphics.Color
import com.example.myhome.R
import com.example.myhome.compose.templates.SensorData
import java.io.Serializable

@kotlinx.serialization.Serializable
data class Device(
    val id :String?= null,
    val name:String?= null,
    val houseId:String?=null,
    val roomId:String?= null,
    val levels: Map<Int, Int>?= null,
    val kwh:Int?= null,
    val status:Boolean?= null,
    val type:String?= null,
    val value: Float?= null,
    val createdAt: TimeDto?= null,
) {
}
fun Device.toSensorData(): List<SensorData> {
    val list = mutableListOf<SensorData>()
    list.add(SensorData(
        name = "Giá trị ",
        value = value.toString(),
        unit = "",
        icon = R.drawable.degree
    ))
    list.add(SensorData(
        name = "Công suất",
        value = kwh.toString(),
        unit = "W",
        icon = R.drawable.power
    ))
    return list
}

@kotlinx.serialization.Serializable
data class TimeDto(
    val _seconds: Long,
    val _nanoseconds: Long
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
){
    val map = mapOf(
        "AUTO" to Color(0xFF4CAF50),
        "MANUAL" to Color(0xFF2196F3),
        "SYSTEM" to Color(0xFFF44336)
    )
    fun convertToColor():Color{
        return map[type]?:Color(0xFF4CAF50)
    }

}