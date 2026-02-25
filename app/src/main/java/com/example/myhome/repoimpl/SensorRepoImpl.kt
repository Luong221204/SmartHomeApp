package com.example.myhome.repoimpl

import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.domain.sensor.Sensor
import com.example.myhome.network.api.SensorService
import com.example.myhome.repository.SensorRepository
import com.example.myhome.util.safeApiCall
import javax.inject.Inject

class SensorRepoImpl @Inject constructor(
    private val service: SensorService
): SensorRepository {
    override suspend fun addNewSensor(sensor: Sensor): NetworkResult<Boolean> {
        return safeApiCall {
            service.addNewSensor(sensor)
        }
    }

    override suspend fun updateSensor(sensor: Sensor): NetworkResult<Boolean> {
        return safeApiCall {
            service.updateSensor(sensor)
        }
    }

    override suspend fun getSensorsByRoomId(roomId: String): NetworkResult<List<Sensor>> {
        return safeApiCall {
            service.getSensorsByRoomId(roomId)
        }
    }

    override suspend fun getSensorDetail(sensorId: String): NetworkResult<Sensor> {
        return safeApiCall {
            service.getSensorDetail(sensorId)
        }
    }
}