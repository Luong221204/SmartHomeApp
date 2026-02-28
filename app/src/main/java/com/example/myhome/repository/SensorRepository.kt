package com.example.myhome.repository

import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.domain.sensor.Sensor

interface SensorRepository {

    suspend fun addNewSensor(sensor: Sensor): NetworkResult<Boolean>

    suspend fun updateSensor(sensor: Sensor): NetworkResult<Boolean>

    suspend fun getSensorsByRoomId(roomId: String): NetworkResult<List<Sensor>>

    suspend fun getSensorDetail(sensorId: String): NetworkResult<Sensor>

    suspend fun getSensorsByHouseId(houseId:String): NetworkResult<List<Sensor>>




}