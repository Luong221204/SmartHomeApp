package com.example.myhome.viewmodel

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.compose.device.SelectState
import com.example.myhome.domain.automation.Automation
import com.example.myhome.domain.automation.Date
import com.example.myhome.domain.device.ActivityLog
import com.example.myhome.domain.device.Device
import com.example.myhome.domain.device.EnergyStat
import com.example.myhome.domain.device.TimeDto
import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.domain.sensor.Sensor
import com.example.myhome.repository.AutomationRepository
import com.example.myhome.repository.DeviceRepository
import com.example.myhome.repository.SensorRepository
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.myhome.compose.templates.ActivityLog as ActivityLogger
@HiltViewModel
class DeviceViewmodel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val automationRepository: Lazy<AutomationRepository>,
    private val sensorRepository: Lazy<SensorRepository>
) : ViewModel() {
    private val _deviceById = MutableStateFlow(DeviceUiState())
    val deviceById = _deviceById.asStateFlow()

    private val _automationScreen = MutableStateFlow(AutoSceneUiState())
    val automationScreen = _automationScreen.asStateFlow()

    private val _buttonLoadingForLog = MutableStateFlow(LoadingMoreButtonUiState())
    val buttonLoadingForLog = _buttonLoadingForLog.asStateFlow()

    private val allLogs =
        MutableStateFlow<List<ActivityLog>>(emptyList()) // Toàn bộ log từ Server

    private val allAutomations =
        MutableStateFlow<List<Automation>>(emptyList()) // Toàn bộ automation từ Server

    private val _visibleCount = MutableStateFlow(2)
    val visibleCount = _visibleCount.asStateFlow()

    private val _visibleCount2 = MutableStateFlow(2)
    val visibleCount2 = _visibleCount2.asStateFlow()

    val displayLogs = combine(allLogs, _visibleCount) { logs, count ->
        logs.take(count)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val displayAutomations = combine(allAutomations, _visibleCount2) { auto, count ->
        auto.take(count)

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _navEvent = MutableSharedFlow<NavEvent>()
    val navEvent = _navEvent.asSharedFlow()

    private val _automationScene = MutableSharedFlow<Resource<Boolean>>()
    val automationScene = _automationScene.asSharedFlow()



    fun onSendAnAutomation(automation: Automation){
        viewModelScope.launch {
            _automationScene.emit(Resource.Loading)
            when(val r = automationRepository.get().createAutomation(automation)){
                is NetworkResult.Success -> {
                    _automationScene.emit(Resource.Success(true))
                }
                is NetworkResult.Error -> {
                    Log.d("DUCLUONG", "onSendAnAutomation: ${r.message}")
                    _automationScene.emit(Resource.Error(r.message))
                }
                else->{

                }
            }
        }
    }
    fun loadLogMore() {
        _visibleCount.value += 2
        if(_visibleCount.value > allLogs.value.size){
            getMoreActivityLogs("FAN_1")
        }
    }

    fun loadAutomationMore(){
        _visibleCount2.value += 2
        if (_visibleCount.value > allAutomations.value.size){
            getMoreAutomationScenes("FAN_1")
        }
    }

    fun hasMoreLog(): Boolean {
        return _visibleCount.value <= allLogs.value.size
    }

    fun onSwitchChange(index: Int, isSelected: Boolean) {

        _automationScreen.update {
            it.copy(listSensor = it.listSensor.mapIndexed { i, sensor ->
                sensor.copy(isSelected = (i == index))
            })
        }

    }

    fun moveToAutomationScreen(houseId: String) {
        _automationScreen.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            when (val result = sensorRepository.get().getSensorsByHouseId(houseId)) {
                is NetworkResult.Success -> {
                    _automationScreen.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            listSensor = result.data
                        )
                    }
                }

                is NetworkResult.Error -> {
                    Log.d("TAG", "moveToAutomationScreen: ${result.message}")

                    _automationScreen.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            error = result.message
                        )
                    }
                }

                else -> {

                }
            }

        }
    }

    fun getDetailDevice(deviceId: String) {
        _deviceById.update {
            it.copy(deviceState = Resource.Loading)
        }
        viewModelScope.launch {
            when (val result = deviceRepository.getDetailDevice(deviceId)) {
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

    fun getMoreActivityLogs(deviceId: String){
        _buttonLoadingForLog.update {
            it.copy(logState = Resource.Loading)
        }
        viewModelScope.launch {
            val startAfter =
                if (allLogs.value.isNotEmpty()) {
                    allLogs.value.last().time.convertTimeToString()
                } else {
                    null
                }
            when (val result = deviceRepository.getActivityLogs(deviceId, _visibleCount.value,startAfter)) {
                is NetworkResult.Success -> {
                    val newList = allLogs.value + result.data
                    allLogs.value = newList
                    _buttonLoadingForLog.update {
                        it.copy(
                            logState = Resource.Success(true),
                            isLogHasMore = _visibleCount.value <= allLogs.value.size
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _buttonLoadingForLog.update {
                        it.copy(
                            logState = Resource.Error(result.message)
                        )
                    }
                }

                else -> {}
            }
        }
    }


    fun getMoreAutomationScenes(deviceId:String){
        _buttonLoadingForLog.update {
            it.copy(automationLoad = Resource.Loading)
        }
        val startAfter =
            if (allAutomations.value.isNotEmpty()) {
                allAutomations.value.last().createdAt?.convertTimeToString()
            } else {
                null
            }
        viewModelScope.launch {
            when(val result = automationRepository.get().getAutomationByDeviceId(deviceId,_visibleCount2.value,startAfter)){
                is NetworkResult.Success -> {
                    val newList = allAutomations.value + result.data
                    allAutomations.value = newList
                    _buttonLoadingForLog.update {
                        it.copy(
                            automationLoad = Resource.Success(true)
                        )
                    }
                }
                is NetworkResult.Error->{
                    _buttonLoadingForLog.update {
                        it.copy(
                            automationLoad = Resource.Error(result.message)
                        )
                    }
                }
                else->{}
            }
        }
    }
    fun getEnergyStat(deviceId: String) {
        _deviceById.update {
            it.copy(energyState = Resource.Loading)
        }
        viewModelScope.launch {
            when (val result = deviceRepository.getEnergyStats(deviceId)) {
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
    fun getActivityLogsInitial(deviceId: String) {
        _deviceById.update {
            it.copy(activityState = Resource.Loading)
        }
        viewModelScope.launch {
            when (val result = deviceRepository.getActivityLogs(deviceId, _visibleCount.value,null)) {
                is NetworkResult.Success -> {
                    Log.d("TAG", "getActivityLogs: ${result.data}")
                    allLogs.value = result.data
                    _buttonLoadingForLog.update {
                        it.copy(
                            logState = Resource.Success(true),
                            isLogHasMore = _visibleCount.value <= allLogs.value.size
                        )
                    }
                    _deviceById.update {
                        it.copy(
                            activityState = Resource.Success(result.data),
                        )
                    }
                }
                is NetworkResult.Error -> {
                    Log.d("TAG", "getActivityLogs: ${result.message}")
                    _deviceById.update {
                        it.copy(
                            activityState = Resource.Error(result.message)
                        )
                    }
                }
                else -> {}
            }
        }

    }

    fun getAutomationScene(deviceId: String){
        _deviceById.update {
            it.copy(automationState = Resource.Loading)
        }
        viewModelScope.launch {
            when(val result = automationRepository.get().getAutomationByDeviceId(deviceId,_visibleCount2.value,null)) {
                is NetworkResult.Success -> {
                    allAutomations.value = result.data
                    Log.d("TAG", "getA: ${result.data}")
                    _deviceById.update {
                        it.copy(
                            automationState = Resource.Success(result.data),
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _deviceById.update { it.copy(automationState = Resource.Error(result.message)) }
                }
                else -> {}
            }
        }
    }

    fun nav(){
        viewModelScope.launch {
            _navEvent.emit(NavEvent.ToAutoScreen)
        }
    }
    fun back(){
        viewModelScope.launch {
            _navEvent.emit(NavEvent.Static)
        }
    }
}
data class Analog(
    val id: String,
    val text: String
)

data class AutoSceneUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val listSensor: List<Sensor> = emptyList(),
)

data class DeviceUiState2(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val device: Device = Device(),
    val error: String? = null,
    val energyStat: List<EnergyStat> = emptyList(),
    val activityLogs: List<ActivityLog> = emptyList(),
    val automationScene : List<Automation> = emptyList()
)

data class DeviceUiState(
    val deviceState: Resource<Device> = Resource.Loading,
    val energyState: Resource<List<EnergyStat>> = Resource.Loading,
    val activityState: Resource<List<ActivityLog>> = Resource.Loading,
    val automationState: Resource<List<Automation>> = Resource.Loading,
)
data class LoadingMoreButtonUiState(
    val logState : Resource<Boolean> = Resource.Loading,
    val automationLoad : Resource<Boolean> = Resource.Loading,
    val isLogHasMore: Boolean = false,
    val isAutomationHasMore: Boolean = false
)


data class SchedulerDto (
    val time: Date?=null,
    val name:String = "",
    val actionCommand:String=""
)



// Sealed class để quản lý trạng thái từng phần
sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    object Idle : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String?) : Resource<Nothing>()
}
fun TimeDto.convertTimeToString(): String{
    return (_seconds*1000+_nanoseconds/1000000).toString()
}


sealed class NavEvent {
    object ToAutoScreen : NavEvent()
    object GoBack : NavEvent()
    object Static: NavEvent()
}