package com.example.myhome.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.R
import com.example.myhome.compose.templates.SensorData
import com.example.myhome.compose.templates.SensorThreshold
import com.example.myhome.domain.device.SafetyLevel
import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.domain.sensor.Sensor
import com.example.myhome.domain.sensor.SeverityLevel
import com.example.myhome.repository.SensorRepository
import com.example.myhome.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SensorViewmodel @Inject constructor(
    private val sensorRepository: SensorRepository
): ViewModel() {
    private val _sensorById = MutableStateFlow(SensorUiState())
    val sensorById = _sensorById.asStateFlow()

    fun setSwitchState(state: Boolean) {
        _sensorById.update {
            it.copy(
                switchState = state
            )
        }
        viewModelScope.launch {
            sensorRepository.updateSensor(Sensor(
                id = _sensorById.value.sensor?.id,
                status = state
            ))
        }
    }
    fun getSensorById(sensorId: String){
        _sensorById.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            when(val result = sensorRepository.getSensorDetail(sensorId)){
                is NetworkResult.Success<Sensor> -> {
                    var threshold : SensorThreshold?=null
                    var safetyLevel : MutableList<SafetyLevel>?=null
                    if(result.data.type?.type.toString() != "DHT11"){
                         threshold = SensorThreshold(
                            min = result.data.type?.min ?: 0f,
                            max = result.data.type?.max ?: 0f,
                            current = result.data.current?.get("analog") ?: 0f
                        )
                    }
                    if(result.data.type?.threshold != null){
                        safetyLevel = mutableListOf()
                        val v = "0-4096"
                        result.data.type.threshold.forEach {
                            val max = it.value.value.split("-")[1].toFloat()
                            val min = it.value.value.split("-")[0].toFloat()
                            safetyLevel.add(
                                SafetyLevel(
                                    max = max,
                                    min = min,
                                    meaning = it.key,
                                    color = it.value.color.toColor()
                                )
                            )
                        }
                    }
                    val list = mutableListOf<SensorData>()
                    result.data.current?.forEach {
                        list.add(SensorData(it.key,
                            it.value.toString(),
                            result.data.type?.unit?.get(it.key).toString(),
                            Constants.unitList.getOrDefault(it.key, R.drawable.analog)
                            ))
                    }

                    val sensor = com.example.myhome.compose.templates.Sensor(
                        id = result.data.id,
                        name = result.data.name ?: "",
                        icon = result.data.type?.image.toString(),
                        status = result.data.status ?: false,
                        threshold = threshold,
                        sensorData = list,
                        data = result.data.data,
                        safetyLevels = safetyLevel
                    )
                    _sensorById.update {
                        it.copy(
                            switchState = result.data.status == true,
                            isLoading = false,
                            sensor = sensor
                        )
                    }
                }
                is NetworkResult.Error -> {
                    Log.d("DUCLUONG", "getSensorById: ${result.message}")
                    _sensorById.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                else -> {
                }
            }
        }
    }
}


data class SensorUiState(
    val sensor: com.example.myhome.compose.templates.Sensor?=null,
    val isLoading: Boolean = false,
    val error: String?=null,
    val isRefreshing: Boolean = false,
    val switchState:Boolean = false
)
fun String.toColor(): Color {
    return try {
        // Loại bỏ tiền tố "0x" hoặc "#" nếu có
        val hexString = this.removePrefix("0x").removePrefix("#")

        // Chuyển String thành số Long với cơ số 16 (Hex)
        val colorLong = hexString.toLong(16)

        // Nếu chuỗi chỉ có 6 ký tự (RRGGBB), ta cần thêm Alpha (FF) vào đầu
        val finalColorLong = if (hexString.length <= 6) {
            0xFF000000 or colorLong
        } else {
            colorLong
        }

        Color(finalColorLong)
    } catch (e: Exception) {
        Color.Gray // Trả về màu mặc định nếu String lỗi
    }
}