package com.example.myhome.network.api

import com.example.myhome.domain.home.House
import com.example.myhome.domain.home.Room
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface HouseService {

    @POST("house/create-house")
    suspend fun createNewHouse(@Body house: House): Boolean

    @POST("house/update-house")
    suspend fun updateHouse(@Body house: House): Boolean

    @POST("house/create-room")
    suspend fun createRoom(@Body room: Room): Boolean

    @GET("house/room")
    suspend fun getRoomsByHouseId(@Query("houseId") houseId: String): List<Room>

    @GET("house")
    suspend fun getHouseInfo(@Query("houseId") houseId: String): House
}

