package com.example.myhome.network.api

import com.example.myhome.domain.User
import com.example.myhome.domain.device.Temperature
import com.example.myhome.network.FcmToken
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body user: User):User


    @GET("home/humid-temp")
    suspend fun getHumidAndTemp() : Response<Temperature>

    @POST("auth/register")
    suspend fun register(@Body user: User): User

    @POST("auth/forgot")
    suspend fun forgot(@Body user: User): User

    @POST("auth/reset-after-forgot")
    suspend fun resetAfterForgot(@Body user: User): User


    @POST("auth/refresh")
    suspend fun refreshToken(@Body() user: User): User

    @PATCH("esp/fcm-token")
    suspend fun updateFcmToken(@Body() fcmToken: FcmToken) : User

    @PATCH("esp/delete-fcm-token")
    suspend fun deleteFcmToken(@Body() fcmToken: FcmToken): User
}