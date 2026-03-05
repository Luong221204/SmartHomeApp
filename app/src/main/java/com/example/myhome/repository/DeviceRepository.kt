package com.example.myhome.repository

import com.example.myhome.domain.device.ActivityLog
import com.example.myhome.domain.device.Device
import com.example.myhome.domain.device.EnergyStat
import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.network.api.Staff

interface DeviceRepository {
    suspend fun getDeviceByRoomId(roomId: String): NetworkResult<List<Device>>
    suspend fun updateDevice(device: Device,how :String): NetworkResult<Boolean>
    suspend fun addDevice(device: Device): NetworkResult<Staff>
    suspend fun getDetailDevice(deviceId: String): NetworkResult<Device>
    suspend fun getEnergyStats(deviceId: String): NetworkResult<List<EnergyStat>>
    suspend fun getActivityLogs(deviceId: String,limit:Int,startAfter:String?):NetworkResult<List<ActivityLog>>
    suspend fun deleteDevice(deviceId: String): NetworkResult<Boolean>
}

