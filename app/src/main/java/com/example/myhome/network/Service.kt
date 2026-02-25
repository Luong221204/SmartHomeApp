package com.example.myhome.network

import com.example.myhome.domain.User
import com.example.myhome.domain.device.Buzzer
import com.example.myhome.domain.device.Device
import com.example.myhome.domain.device.Door
import com.example.myhome.domain.device.EnergyStat
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
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
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
    suspend fun start() : Response<User>

    @GET("home/password")
    suspend fun getPassword() : Response<Password>

    @GET("home/temp-chart")
    suspend fun getTemp() : Response<Temperature>

    @GET("home/humid-chart")
    suspend fun getHumid() : Response<Humidity>
//----------------------------------------------------------------------------
    @POST("auth/login")
    suspend fun login(@Body user: User): Response<User>


    @GET("home/humid-temp")
    suspend fun getHumidAndTemp() : Response<Temperature>

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

