package com.example.myhome.repoimpl

import com.example.myhome.domain.device.Device
import com.example.myhome.domain.device.EnergyStat
import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.network.ApiConnect
import com.example.myhome.network.api.DeviceService
import com.example.myhome.repository.DeviceRepository
import com.example.myhome.util.safeApiCall
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DeviceRepoImpl @Inject constructor(
    private val deviceService: DeviceService
) : DeviceRepository {
    override suspend fun getDeviceByRoomId(roomId: String): NetworkResult<List<Device>> {
        return safeApiCall {
            deviceService.getDevicesByRoomId(roomId)
        }
    }

    override suspend fun updateDevice(device: Device): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun addDevice(device: Device): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getDetailDevice(deviceId: String): NetworkResult<Device> {
        return safeApiCall {
            deviceService.getDetailDevice(deviceId)
        }
    }

    override suspend fun getEnergyStats(deviceId: String): NetworkResult<List<EnergyStat>> {
        TODO("Not yet implemented")
    }
}

