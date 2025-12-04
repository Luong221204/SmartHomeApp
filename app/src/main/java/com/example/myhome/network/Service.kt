package com.example.myhome.network

import com.example.myhome.domain.device.Buzzer
import com.example.myhome.domain.device.Door
import com.example.myhome.domain.device.Fan
import com.example.myhome.domain.device.FlameSensor
import com.example.myhome.domain.device.GasSensor
import com.example.myhome.domain.device.Humidity
import com.example.myhome.domain.device.Led
import com.example.myhome.domain.device.Password
import com.example.myhome.domain.response.Model
import com.example.myhome.domain.device.Pump
import com.example.myhome.domain.device.RainSensor
import com.example.myhome.domain.device.Temperature
import com.example.myhome.domain.response.Status
import retrofit2.Response
import retrofit2.http.Body
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

    @POST("home/update-fs/level")
    suspend fun updateFsLevel(@Body fs: FlameSensor): Response<Model>

    @POST("home/update-rs")
    suspend fun updateRs(@Body rs: RainSensor): Response<Model>

    @POST("home/update-rs/level")
    suspend fun updateRsLevel(@Body rs: RainSensor): Response<Model>

    @POST("home/update-buz")
    suspend fun updateBuz(@Body rs: Buzzer): Response<Model>

    @POST("home/update-gs")
    suspend fun updateGs(@Body gs: GasSensor): Response<Model>

    @POST("home/update-gs/level")
    suspend fun updateGsLevel(@Body gs: GasSensor): Response<Model>

    @POST("home/update-led")
    suspend fun updateLedAt(@Body led: Led ): Response<Model>



    @POST("home/update-password")
    suspend fun changePassword
                (@Body() content: Password,
                 ):Response<Model>


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


    @GET("home/buz-status")
    suspend fun getBuzz() : Response<Buzzer>
    @GET("home")
    suspend fun start() : Response<Model>

    @GET("home/password")
    suspend fun getPassword() : Response<Password>

    @GET("home/temp-chart")
    suspend fun getTemp() : Response<Temperature>

    @GET("home/humid-chart")
    suspend fun getHumid() : Response<Humidity>



}

