package com.example.myhome.repository

import com.example.myhome.domain.home.House
import com.example.myhome.domain.home.Room
import com.example.myhome.domain.response.NetworkResult

interface HouseRepository {
    suspend fun createNewHouse(house: House): NetworkResult<Boolean>
    suspend fun updateHouse(house: House): NetworkResult<Boolean>
    suspend fun createRoom(room: Room): NetworkResult<Boolean>
    suspend fun getRoomsByHouseId(houseId: String): NetworkResult<List<Room>>
    suspend fun getHouseInfo(houseId: String): NetworkResult<House>
}

