package com.example.myhome.repository

import com.example.myhome.domain.home.House
import com.example.myhome.domain.home.Room
import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.network.api.Staff

interface HouseRepository {
    suspend fun createNewHouse(house: House): NetworkResult<Boolean>
    suspend fun updateHouse(house: House): NetworkResult<Boolean>
    suspend fun createRoom(room: Room): NetworkResult<Room>
    suspend fun getRoomsByHouseId(houseId: String): NetworkResult<List<Room>>
    suspend fun getHouseInfo(houseId: String): NetworkResult<House>
    suspend fun getStaffByRoomId(roomId: String): NetworkResult<List<Staff>>
    suspend fun deleteRoom(room: String): NetworkResult<Boolean>
}

