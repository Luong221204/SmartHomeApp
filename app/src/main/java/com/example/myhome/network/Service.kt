package com.example.myhome.network

import com.example.myhome.domain.User
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

interface Service {





    @GET("home")
    suspend fun start() : Response<User>


//----------------------------------------------------------------------------
    @POST("auth/login")
    suspend fun login(@Body user: User): Response<User>



    @POST("auth/register")
    suspend fun register(@Body user: User): Response<User>

    @POST("auth/forgot")
    suspend fun forgot(@Body user: User): Response<User>

    @POST("auth/reset-after-forgot")
    suspend fun resetAfterForgot(@Body user: User): Response<User>


    @POST("auth/refresh")
    suspend fun refreshToken(@Body() user: User): Response<User>

    @PATCH("esp/fcm-token")
    suspend fun updateFcmToken(@Body() fcmToken: FcmToken) : Response<User>

    @PATCH("esp/delete-fcm-token")
    suspend fun deleteFcmToken(@Body() fcmToken: FcmToken): Response<User>
//--------------------------------------------------------------------------------


    @POST("device/add")
    suspend fun addNewDevice(@Body() device: Device): Response<Model>


    @GET("device")
    suspend fun getDevicesByRoomId(@Query("roomId") roomId: String): List<Device>


    @GET("device/detail")
    suspend fun getDetailDevice(@Query("deviceId") deviceId: String): Response<Device>


    @PATCH("device/update")
    suspend fun updateDevice(@Body() device: Device): Response<Model>

    @DELETE("device/delete")
    suspend fun deleteDevice(@Query("deviceId") deviceId: String): Response<Model>

    @GET("device/energy-stat")
    suspend fun getEnergyStat(@Query("deviceId") deviceId: String): Response<List<EnergyStat>>
//----------------------------------------------------------------------------------



}

data class FcmToken(
    val fcmToken: String?,
    val userId: String
)

