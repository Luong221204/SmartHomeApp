package com.example.myhome.network.api

import com.example.myhome.domain.sensor.Sensor
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SensorService {

    @POST("sensor/add")
    suspend fun addNewSensor(@Body() sensor: Sensor): Boolean

    @GET("sensor")
    suspend fun getSensorsByRoomId(@Query("roomId") roomId: String): List<Sensor>

    @GET("sensor")
    suspend fun getSensorsByHouseId(@Query("houseId") houseId: String): List<Sensor>

    @GET("sensor/detail")
    suspend fun getSensorDetail(@Query("sensorId") sensorId: String): Sensor


    @POST("sensor/update")
    suspend fun updateSensor(@Body() sensor: Sensor): Boolean

}