package com.example.myhome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.compose.device.SelectState
import com.example.myhome.domain.device.ActivityLog
import com.example.myhome.domain.device.Device
import com.example.myhome.domain.device.EnergyStat
import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.repository.AutomationRepository
import com.example.myhome.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceViewmodel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val automationRepository: AutomationRepository
) : ViewModel(){
    private val _deviceById = MutableStateFlow(DeviceUiState())
    val deviceById = _deviceById.asStateFlow()

    private val _x = MutableStateFlow(SelectState())
    val x= _x.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2000)
            _x.update {
                it.copy(list = listOf(true,false,false))
            }
        }
    }
    fun getDetailDevice(deviceId: String){
        _deviceById.update {
            it.copy(deviceState  = Resource.Loading)
        }
        viewModelScope.launch {
            when(val result = deviceRepository.getDetailDevice(deviceId)) {
                is NetworkResult.Success -> {
                    _deviceById.update {
                        it.copy(
                            deviceState = Resource.Success(result.data),
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _deviceById.update {
                        it.copy(
                           deviceState = Resource.Error(result.message)
                        )
                    }
                }
                else -> {

                }
            }
        }
    }
    fun getEnergyStat(deviceId: String) {
        _deviceById.update {
            it.copy(energyState = Resource.Loading)
        }
        viewModelScope.launch {
            when(val result = deviceRepository.getEnergyStats(deviceId)) {
                is NetworkResult.Success -> {
                    _deviceById.update {
                        it.copy(
                            energyState = Resource.Success(result.data),
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _deviceById.update {
                        it.copy(
                            energyState = Resource.Error(result.message)
                        )
                    }
                }
                else -> {
                }
            }
        }
    }
}

data class DeviceUiState2(
    val isLoading:Boolean = false,
    val isSuccess:Boolean = false,
    val device : Device = Device(),
    val error :String? = null,
    val energyStat: List<EnergyStat> = emptyList(),
    val activityLogs:List<ActivityLog> = emptyList()
)
data class DeviceUiState(
    val deviceState: Resource<Device> = Resource.Loading,
    val energyState: Resource<List<EnergyStat>> = Resource.Loading,
    val activityState: Resource<List<ActivityLog>> = Resource.Loading
)

// Sealed class để quản lý trạng thái từng phần
sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String?) : Resource<Nothing>()
}