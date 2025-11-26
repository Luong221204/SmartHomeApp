package com.example.myhome.network

import com.example.myhome.domain.Door
import com.example.myhome.domain.Fan
import com.example.myhome.domain.FlameSensor
import com.example.myhome.domain.GasSensor
import com.example.myhome.domain.Led
import com.example.myhome.domain.response.Model
import com.example.myhome.domain.Pump
import com.example.myhome.domain.RainSensor
import com.example.myhome.domain.response.Status
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Service {

    @POST("home/update-pump")
    suspend fun updatePump(@Body pump: Pump ): Response<Model>

    @POST("home/update-fan ")
    suspend fun updateFan(@Body fan: Fan ): Response<Model>

    @POST("home/update-door")
    suspend fun updateDoor(@Body door: Door) : Response<Model>


    @POST("home/update-fs")
    suspend fun updateFs(@Body fs: FlameSensor): Response<Model>

    @POST("home/update-rs")
    suspend fun updateRs(@Body rs: RainSensor): Response<Model>

    @POST("home/update-gs")
    suspend fun updateGs(@Body gs: GasSensor): Response<Model>

    @POST("home/update-led")
    suspend fun updateLedAt(@Body led: Led ): Response<Model>

    @POST("home/change-password")
    suspend fun changePassword
                (@Field("newPassword") newPassword:String,
                 @Field("oldPassword") oldPassword:String):Response<Model>


    @GET("home/pump-status")
    suspend fun getPump() : Response<Status>

    @GET("home/fan-status")
    suspend fun getFan() : Response<Status>

    @GET("home/door-status")
    suspend fun getDoor() : Response<Status>


    @GET("home/led-status")
    suspend fun getLedAt(@Query("location") location:String) : Response<Led>

    @GET("home/fs-status")
    suspend fun getFlameSensor() : Response<FlameSensor>

    @GET("home/gs-status")
    suspend fun getGasSensor() : Response<GasSensor>

    @GET("home/rs-status")
    suspend fun getRainSensor() : Response<RainSensor>

}