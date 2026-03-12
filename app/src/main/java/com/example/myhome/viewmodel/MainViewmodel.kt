package com.example.myhome.viewmodel

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.R
import com.example.myhome.domain.User
import com.example.myhome.domain.device.Device
import com.example.myhome.domain.home.House
import com.example.myhome.domain.home.Room
import com.example.myhome.domain.response.Model
import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.domain.response.Notification
import com.example.myhome.domain.response.Result
import com.example.myhome.domain.sensor.Sensor
import com.example.myhome.domain.voice.Requires
import com.example.myhome.local.DataManager
import com.example.myhome.local.DataManager2
import com.example.myhome.network.ApiConnect
import com.example.myhome.network.FcmToken
import com.example.myhome.network.api.Staff
import com.example.myhome.repository.DeviceRepository
import com.example.myhome.repository.HouseRepository
import com.example.myhome.repository.SensorRepository
import com.example.myhome.repository.SocketRepository
import com.example.myhome.service.SocketHandler
import com.example.myhome.ui.theme.Brown
import com.example.myhome.ui.theme.DeviceColor
import com.example.myhome.ui.theme.EmergencyColor
import com.example.myhome.util.Constants
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import kotlin.jvm.java

data class HouseUiState(
    val houseInfoState: Resource<House> = Resource.Idle,
    val listRoomState: Resource<List<Room>> = Resource.Idle,
    val listStaffState: Resource<List<Staff>> = Resource.Idle
)
sealed class MainEvent{
    object HomeScreenEvent: MainEvent()
    object NotificationScreenEvent: MainEvent()
    object AccountScreenEvent: MainEvent()
    data class RoomDetailEvent(val roomId: String): MainEvent()
    data class LeaveRoomEvent(val roomId: String): MainEvent()
}


data class MainState(
    val houseUiState: HouseUiState = HouseUiState(),
    val roomDetailState: Map<String,Resource<List<Staff>>> = mapOf(),
    val notificationState:Resource<Notification> = Resource.Idle,
    val accountState:Resource<User> = Resource.Idle,
)

data class VoiceData(
    val listRoom:List<String> = emptyList(),
    val listName:List<String> = emptyList()
)
@HiltViewModel
class MainViewmodel @Inject constructor(
    private val repository: HouseRepository,
    private val local : DataManager2,
    private val deviceRepository: dagger.Lazy<DeviceRepository>,
    private val sensorRepository: dagger.Lazy<SensorRepository>,
    private val socketRepository: dagger.Lazy<SocketRepository>
) : ViewModel() {

    private var socket: Socket = SocketHandler.getSocket()
    var text by mutableStateOf("Xin chào")

    private val _response = MutableSharedFlow<Result>()
    val response: SharedFlow<Result> = _response

    private val _mainState = MutableStateFlow<MainState>(MainState())
    val mainState = _mainState.asStateFlow()

    val mapRoom = mutableMapOf<String, MutableStateFlow<Resource<List<Staff>>>>()

    private val _addNewState = MutableSharedFlow<Resource<Boolean>>()
    val addNewState = _addNewState.asSharedFlow()

    private val _voiceData = MutableStateFlow<VoiceData>(VoiceData())
    private val message = MutableSharedFlow<String>()
    private lateinit var job  : Job
    init {
        socketRepository.get().connect()
    }

    fun sendMessage(m:String){
        val jsonSchema = Schema.obj(
            mapOf("requires" to Schema.array(
                Schema.obj(
                    mapOf(
                        "name" to Schema.enumeration(
                            _voiceData.value.listName
                        ),
                        "status" to Schema.boolean(),
                        "roomName" to Schema.enumeration(
                            _voiceData.value.listRoom
                        ),
                        "value" to Schema.integer()
                    ),
                )
            ))
        )
        val model = Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel("gemini-2.5-flash",
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                    responseSchema = jsonSchema
                },

                )
        viewModelScope.launch {
            if(!::job.isInitialized) return@launch
            if(job.isActive) {
                Log.d("TAGS","isActive")
                job.join()
            }
            Log.d("TAGS", _voiceData.value.listName.toString())
            Log.d("TAGS", _voiceData.value.listRoom.toString())

            Log.d("TAGS", m)
            try {
                val response = model.generateContent(m)
                response.text?.let { it ->
                    Log.d("TAGS", it)
                    val data = Gson().fromJson(it, Staff::class.java)
                    Log.d("TAGS","$data")
                }
            }catch (e: Exception){
                Log.d("TAGS","$e")
            }

        }
    }
    fun start(){
        job= viewModelScope.launch {
            Log.d("TAGS","start")
            delay(10000)
            Log.d("TAGS","complete")
        }
    }

    fun getHouseInfo(){
        if(mainState.value.houseUiState.houseInfoState is Resource.Success) return
        if(mainState.value.houseUiState.houseInfoState is Resource.Loading) return
        val currentHouseId = local.getCurrentHouseId() ?: return
        viewModelScope.launch {
            job = launch(Dispatchers.Default) {
                when(val r = repository.getStaffByHouseId(currentHouseId)){
                    is NetworkResult.Error -> {
                        _mainState.update {
                            it.copy(houseUiState = it.houseUiState.copy(listStaffState = Resource.Error(r.message)))
                        }
                    }
                    is NetworkResult.Success -> {
                        _mainState.update {
                            it.copy(houseUiState = it.houseUiState.copy(listStaffState = Resource.Success(r.data)))
                        }
                        _voiceData.update {
                            it.copy(
                                listName = r.data.map { it.name?:"" },
                                listRoom = r.data.map { it.roomName?:"" }
                            )
                        }
                    }
                    else->{}
                }
            }
            launch(Dispatchers.Default) {
                _mainState.update {
                    it.copy(
                        houseUiState = it.houseUiState.copy(houseInfoState = Resource.Loading)
                    )
                }
                when(val r = repository.getHouseInfo(currentHouseId)){
                    is NetworkResult.Error -> {
                        _mainState.update {
                            it.copy(houseUiState = it.houseUiState.copy(houseInfoState = Resource.Error(r.message)))
                        }
                    }
                    is NetworkResult.Success -> {
                        _mainState.update {
                            it.copy(houseUiState = it.houseUiState.copy(houseInfoState = Resource.Success(r.data)))
                        }
                    }
                    else->{}
                }
            }

        }
    }

    fun addNewDeviceOrSensor(name:String,type:String,kind:String,roomId:String){
        val houseId = local.getCurrentHouseId()
        viewModelScope.launch {
            _addNewState.emit(Resource.Loading)
            when(val r =
                if(kind == "DEVICE")
                    deviceRepository.get()
                        .addDevice(Device( name = name,type = type, houseId = houseId,roomId = roomId)
                        ) else sensorRepository.get().addNewSensor(Sensor(
                    name = name, refferTo = Constants.deviceName[type], houseId = houseId, roomId = roomId,
                    kind = type
                ))){
                is NetworkResult.Error -> {
                    _addNewState.emit(Resource.Error(r.message))
                }
                is NetworkResult.Success<Staff> -> {
                    val x = mapRoom[roomId]?.value
                    if(x is Resource.Success){
                        mapRoom[roomId]?.value = Resource.Success(x.data +r.data)
                    }
                    _mainState.value.houseUiState.copy(
                        houseInfoState = Resource.Idle
                    )
                    getHouseInfo()
                    _addNewState.emit(Resource.Success(true))
                }
                else->{}
            }
        }

    }

    fun deleteHardware(roomId: String, staff: Staff){
        viewModelScope.launch {
            _addNewState.emit(Resource.Loading)
            when(val r =if(staff.kind == "DEVICE")deviceRepository.get().deleteDevice(staff.id?:"")
            else sensorRepository.get().deleteSensor(staff.id?:"")){

                is NetworkResult.Error -> {
                    _addNewState.emit(Resource.Error(r.message))
                }
                is NetworkResult.Success -> {
                    val x = mapRoom[roomId]?.value
                    if(x is Resource.Success){
                        mapRoom[roomId]?.value = Resource.Success(x.data -staff)
                    }
                    _mainState.value.houseUiState.copy(
                        houseInfoState = Resource.Idle
                    )
                    getHouseInfo()
                    _addNewState.emit(Resource.Success(true))
                }
                else->{}
            }
        }
    }

    fun deleteRoom(room: Room){
        viewModelScope.launch {
            _addNewState.emit(Resource.Loading)
            when(val r = repository.deleteRoom(room.id?:"")){

                is NetworkResult.Error -> {
                    _addNewState.emit(Resource.Error(r.message))
                }
                is NetworkResult.Success -> {
                    val x : MutableList<Room> = mutableListOf()
                    val h = _mainState.value.houseUiState.listRoomState
                    if(h is Resource.Success){
                        x.addAll(h.data)
                    }
                    x.remove(room)
                    _mainState.update {
                        it.copy(houseUiState = it.houseUiState.copy(listRoomState = Resource.Success(x)))
                    }
                    _mainState.value.houseUiState.copy(
                        houseInfoState = Resource.Idle
                    )
                    getHouseInfo()
                    _addNewState.emit(Resource.Success(true))
                }
                else->{}
            }
        }
    }

    fun updateHardware(
        staffs: List<Staff>
    ){
        val listData = JSONArray()
        staffs.forEach {
            listData.put(Gson().toJson(it))
        }
        socketRepository.get().sendMessage("messageFromMobile",listData)
        val groupedByRoom = staffs.groupBy { it.roomId }
        groupedByRoom.forEach { (roomId, staffListInRoom) ->
            val currentResource = mapRoom[roomId]?.value

            if (currentResource is Resource.Success) {
                val updatedList = currentResource.data.toMutableList()

                staffListInRoom.forEach { incomingStaff ->
                    val index = updatedList.indexOfFirst { it.id == incomingStaff.id }
                    if (index != -1) {
                        updatedList[index] = updatedList[index].copy(
                            status = incomingStaff.status,
                            value = incomingStaff.value
                        )
                    }
                }

                mapRoom[roomId]?.value = Resource.Success(updatedList)

            }
        }
    }

    fun addNewRoom(name:String,type:String){
        val houseId = local.getCurrentHouseId() ?: return
        viewModelScope.launch {
           _addNewState.emit(Resource.Loading)
            when(val r = repository.createRoom(Room(
                name = name, type = type, houseId = houseId
            ))){

                is NetworkResult.Error -> {

                    _addNewState.emit(Resource.Error(r.message))
                }
                is NetworkResult.Success -> {
                    val x : MutableList<Room> = mutableListOf()
                    val h = _mainState.value.houseUiState.listRoomState
                    if(h is Resource.Success){
                        x.addAll(h.data)
                    }
                    x.add(r.data)
                    _mainState.update {
                        it.copy(houseUiState = it.houseUiState.copy(listRoomState = Resource.Success(x)))
                    }
                    _mainState.value.houseUiState.copy(
                        houseInfoState = Resource.Idle
                    )
                    getHouseInfo()
                    _addNewState.emit(Resource.Success(true))
                }
                else->{}
            }
        }
    }
    fun getListRoom(){
        if(mainState.value.houseUiState.listRoomState is Resource.Success) return
        if(mainState.value.houseUiState.listRoomState is Resource.Loading) return
        val currentHouseId = local.getCurrentHouseId() ?: return
        viewModelScope.launch {
            _mainState.update {
                it.copy(
                    houseUiState = it.houseUiState.copy(listRoomState = Resource.Loading)
                )
            }
            when(val r = repository.getRoomsByHouseId(currentHouseId)){
                is NetworkResult.Error -> {
                    _mainState.update {
                        it.copy(houseUiState = it.houseUiState.copy(listRoomState = Resource.Error(r.message)))
                    }
                }
                is NetworkResult.Success -> {
                    r.data.forEach {
                        mapRoom[it.id?:"0"] = MutableStateFlow(Resource.Idle)
                    }


                    _mainState.update {
                        it.copy(houseUiState = it.houseUiState.copy(listRoomState = Resource.Success(r.data)))
                    }

                }
                else->{}
            }
        }
    }

    fun switchScreen(event:MainEvent){
        when(event){
            is MainEvent.HomeScreenEvent->{
                getListRoom()
                getHouseInfo()
            }
            is MainEvent.NotificationScreenEvent->{
                getNotification()
            }
            is MainEvent.RoomDetailEvent ->{
                socketRepository.get().sendMessage("subscribe_room",event.roomId)
                getRoomDetail(event.roomId)
                socketRepository.get().listenEvent("room_update"){
                    it->
                    Log.d("TAGS","$it")
                    handleSocketData(event.roomId,it)
                }
            }
            is MainEvent.LeaveRoomEvent->{
                socketRepository.get().sendMessage("unsubscribe_room",event.roomId)
            }
            else->{

            }
        }
    }
    private fun handleSocketData(roomId: String,data: Any) {
        val gson = Gson()

        val listType = object : TypeToken<List<Staff>>() {}.type
        val incomingStaffList: List<Staff> = gson.fromJson(data.toString(), listType)

        if (incomingStaffList.isEmpty()) return

        val currentResource = mapRoom[roomId]?.value

        if (currentResource is Resource.Success) {
            val updatedList = currentResource.data.toMutableList()

            incomingStaffList.forEach { incomingStaff ->
                val index = updatedList.indexOfFirst { it.id == incomingStaff.id }
                if (index != -1) {
                    updatedList[index] = updatedList[index].copy(
                        status = incomingStaff.status,
                        value = incomingStaff.value
                    )
                }
            }
            mapRoom[roomId]?.value = Resource.Success(updatedList)
            Log.d("TAGS","${mapRoom[roomId]?.value}")

        }
    }

    private fun getRoomDetail(roomId: String) {
        if(mapRoom.containsKey(roomId)
            && mapRoom[roomId]?.value is Resource.Success) return
        if(mapRoom.containsKey(roomId)
            && mapRoom[roomId]?.value is Resource.Loading) return
        viewModelScope.launch {
            mapRoom[roomId]?.value = Resource.Loading

            when(val r = repository.getStaffByRoomId(roomId)){
                is NetworkResult.Error -> {
                    mapRoom[roomId]?.value = Resource.Error(r.message)
                    _mainState.update {
                        it.copy(roomDetailState = it.roomDetailState + (roomId to Resource.Error(r.message))
                        )
                    }
                }
                is NetworkResult.Success -> {
                    mapRoom[roomId]?.value = Resource.Success(r.data)
                    Log.d("DUCLUONG"," RoomDetailScreen ${mapRoom[roomId]}")

                    _mainState.update {
                        it.copy(roomDetailState = it.roomDetailState + (roomId to Resource.Success(r.data)))
                    }
                }
                else->{}
            }
        }

    }

    private fun getNotification(){
        if(mainState.value.notificationState is Resource.Success) return
        if(mainState.value.notificationState is Resource.Loading) return


    }
    suspend fun logout(): Boolean {
        socket.disconnect()
        socket.off()
        socket.close()
        val r = ApiConnect.service!!.deleteFcmToken(
            FcmToken(
                DataManager.getFcmToken(),
                DataManager.getUser().id
            )
        ).isSuccessful
        DataManager.saveLoginStatus(false)
        DataManager.saveUser(User())
        DataManager.saveFcmToken("")
        return r
    }

    fun updateText(s:String){
        text = s
    }




}