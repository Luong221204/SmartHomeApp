package com.example.myhome.network.api

import com.example.myhome.domain.automation.Automation
import com.example.myhome.domain.device.Device
import com.example.myhome.domain.device.EnergyStat
import com.example.myhome.domain.response.Model
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface DeviceService {
    @POST("device/add")
    suspend fun addNewDevice(@Body() device: Device): Response<Model>


    @GET("device")
    suspend fun getDevicesByRoomId(@Query("houseId") houseId: String): List<Device>


    @GET("device/detail")
    suspend fun getDetailDevice(@Query("deviceId") deviceId: String): Device


    @PATCH("device/update")
    suspend fun updateDevice(@Body() device: Device): Response<Model>

    @DELETE("device/delete")
    suspend fun deleteDevice(@Query("deviceId") deviceId: String): Response<Model>

    @GET("device/energy-stat")
    suspend fun getEnergyStat(@Query("deviceId") deviceId: String): List<EnergyStat>

}