package com.example.myhome.repository

import com.example.myhome.domain.device.Device
import com.example.myhome.domain.device.EnergyStat
import com.example.myhome.domain.response.NetworkResult

interface DeviceRepository {
    suspend fun getDeviceByRoomId(roomId: String): NetworkResult<List<Device>>
    suspend fun updateDevice(device: Device): Boolean
    suspend fun addDevice(device: Device): Boolean
    suspend fun getDetailDevice(deviceId: String): NetworkResult<Device>
    suspend fun getEnergyStats(deviceId: String): NetworkResult<List<EnergyStat>>

}