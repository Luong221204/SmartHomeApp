package com.example.myhome.repoimpl

import com.example.myhome.domain.home.House
import com.example.myhome.domain.home.Room
import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.network.api.HouseService
import com.example.myhome.repository.HouseRepository
import com.example.myhome.util.safeApiCall
import javax.inject.Inject

class HouseRepoImpl @Inject constructor(
    private val houseService: HouseService
): HouseRepository {
    override suspend fun createNewHouse(house: House): NetworkResult<Boolean> {
        return safeApiCall { houseService.createNewHouse(house) }
    }

    override suspend fun updateHouse(house: House): NetworkResult<Boolean> {
        return safeApiCall { houseService.updateHouse(house) }    }

    override suspend fun createRoom(room: Room): NetworkResult<Boolean> {
        return safeApiCall { houseService.createRoom(room) }    }

    override suspend fun getRoomsByHouseId(houseId: String): NetworkResult<List<Room>> {
        return safeApiCall { houseService.getRoomsByHouseId(houseId) }    }

    override suspend fun getHouseInfo(houseId: String): NetworkResult<House> {
        return safeApiCall { houseService.getHouseInfo(houseId) }
    }


}