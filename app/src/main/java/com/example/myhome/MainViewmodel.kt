package com.example.myhome

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.domain.Door
import com.example.myhome.domain.Fan
import com.example.myhome.domain.FlameSensor
import com.example.myhome.domain.GasSensor
import com.example.myhome.domain.General
import com.example.myhome.domain.Led
import com.example.myhome.domain.Pump
import com.example.myhome.domain.RainSensor
import com.example.myhome.domain.response.Model
import com.example.myhome.domain.response.TempAndHumid
import com.example.myhome.network.ApiConnect
import com.example.myhome.service.SocketHandler
import com.example.myhome.ui.theme.Brown
import com.google.gson.Gson
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class MainViewmodel : ViewModel() {
    private var socket: Socket = SocketHandler.getSocket()
    var temp by mutableStateOf("")
        private set

    var humid by mutableStateOf("")
        private set


    var light1 = mutableStateOf(false)// living room
        private set

    var light2 = mutableStateOf(false) // bedroom
        private set

    var pump = mutableStateOf(false)
        private set

    var fan = mutableStateOf(false)
        private set

    var door = mutableStateOf(false)
        private set

    var fs = mutableStateOf(false)
        private set

    var gs = mutableStateOf(false)
        private set

    var rs = mutableStateOf(false)
        private set


    private val _fanResponse = MutableSharedFlow<Result>()
    val fanResponse = _fanResponse

    private val _pumpResponse = MutableSharedFlow<Result>()
    val pumpResponse = _pumpResponse

    private val _doorResponse = MutableSharedFlow<Result>()
    val doorResponse = _doorResponse

    private val _light1Response = MutableSharedFlow<Result>()
    val light1Response = _light1Response

    private val _light2Response = MutableSharedFlow<Result>()
    val light2Response = _light2Response

    private val _rsResponse = MutableSharedFlow<Result>()
    val rsResponse = _rsResponse

    private val _gsResponse = MutableSharedFlow<Result>()
    val gsResponse = _gsResponse

    private val _fsResponse = MutableSharedFlow<Result>()
    val fsResponse = _fsResponse

    init {
        socket.on(Socket.EVENT_CONNECT) {
            Log.d("DUCLUONG", "Connected to NestJS")
        }
        getTemperatureAndHumidity()
        getDoorStatus()
        getLedAt()
        getFanStatus()
        getPumpStatus()
        getGsStatus()
        getRsStatus()
        getFsStatus()

    }



    fun updateFan(status: Boolean){
        viewModelScope.launch{
            _fanResponse.emit(Result.Loading)
            try {
                val result = ApiConnect.service!!.updateFan(Fan(status = status))
                val res = Result.Response<Response<Model>>(result)
                _fanResponse.emit(res)
                res.t.body()?.apply {
                    if(success){
                        fan.value = status
                    }
                }
            } catch (e: Exception) {
                _fanResponse.emit(Result.Error)
            }
        }
    }

    fun updatePump(status: Boolean){
        viewModelScope.launch{
            _pumpResponse.emit(Result.Loading)
            try {
                val result = ApiConnect.service!!.updatePump(Pump(status = status))
                val res = Result.Response<Response<Model>>(result)
                _pumpResponse.emit(res)
                res.t.body()?.apply {
                    if(success){
                        pump.value  = status
                    }
                }
            } catch (e: Exception) {
                _pumpResponse.emit(Result.Error)
            }
        }
    }

    fun updateDoor(status: Boolean){
        viewModelScope.launch{
            _doorResponse.emit(Result.Loading)
            try {
                val result = ApiConnect.service!!.updateDoor(Door(status = status))
                val res = Result.Response<Response<Model>>(result)
                _doorResponse.emit(res)
                res.t.body()?.apply {
                    if(success){
                        door.value  = status
                    }
                }
                Log.d("DUCLUONG", "${result.body()?.success}")
            } catch (e: Exception) {
                Log.e("DUCLUONG", "Error: $e")
                _doorResponse.emit(Result.Error)
            }
        }
    }

    fun updateLightAtLivingRoom(status: Boolean){
        viewModelScope.launch{
            _light1Response.emit(Result.Loading)
            try {
                val result = ApiConnect.service!!.updateLedAt(Led(status = status,"living room"))
                val res = Result.Response<Response<Model>>(result)
                _light1Response.emit(res)
                res.t.body()?.apply {
                    if(success){
                        light1.value  = status
                    }
                }
                Log.d("DUCLUONG", "${result.body()?.success}")
            } catch (e: Exception) {
                Log.e("DUCLUONG", "Error: $e")
                _light1Response.emit(Result.Error)
            }
        }
    }

    fun updateLightAtBedRoom(status: Boolean){
        viewModelScope.launch{
            _light2Response.emit(Result.Loading)
            try {
                val result = ApiConnect.service!!.updateLedAt(Led(status = status,"bedroom"))
                val res = Result.Response<Response<Model>>(result)
                _light2Response.emit(res)
                res.t.body()?.apply {
                    if(success){
                        light2.value  = status
                    }
                }
                Log.d("DUCLUONG", "${result.body()?.success}")
            } catch (e: Exception) {
                Log.e("DUCLUONG", "Error: $e")
                _light2Response.emit(Result.Error)
            }
        }
    }

    fun updateFsStatus(status: Boolean) {
        viewModelScope.launch {
            _fsResponse.emit(Result.Loading)
            try {
                val result = ApiConnect.service!!.updateFs(FlameSensor(status = status))
                val res = Result.Response<Response<Model>>(result)
                _fsResponse.emit(res)
                res.t.body()?.apply {
                    if (success) {
                        fs.value  = status
                    }
                }
                Log.d("DUCLUONG", "FS updated: ${result.body()?.success}")
            } catch (e: Exception) {
                Log.e("DUCLUONG", "FS Error: $e")
                _fsResponse.emit(Result.Error)
            }
        }
    }

    fun updateGsStatus(status: Boolean) {
        viewModelScope.launch {
            _gsResponse.emit(Result.Loading)
            try {
                val result = ApiConnect.service!!.updateGs(GasSensor(status = status))
                val res = Result.Response<Response<Model>>(result)
                _gsResponse.emit(res)
                res.t.body()?.apply {
                    if (success) {
                        gs.value  = status
                    }
                }
                Log.d("DUCLUONG", "GS updated: ${result.body()?.success}")
            } catch (e: Exception) {
                Log.e("DUCLUONG", "GS Error: $e")
                _gsResponse.emit(Result.Error)
            }
        }
    }

    fun updateRsStatus(status: Boolean) {
        viewModelScope.launch {
            _rsResponse.emit(Result.Loading)
            try {
                val result = ApiConnect.service!!.updateRs(RainSensor(status = status))
                val res = Result.Response<Response<Model>>(result)
                _rsResponse.emit(res)
                res.t.body()?.apply {
                    if (success) {
                        rs.value  = status
                    }
                }
                Log.d("DUCLUONG", "RS updated: ${result.body()?.success}")
            } catch (e: Exception) {
                Log.e("DUCLUONG", "RS Error: $e")
                _rsResponse.emit(Result.Error)
            }
        }
    }

    private fun getDoorStatus(){
        viewModelScope.launch {
            door.value = ApiConnect.service!!.getDoor().body()?.status == true

        }

        socket.on("doorStatusUpdate"){ args ->
            val msg = args[0] as JSONObject
            val d = Gson().fromJson(msg.toString(), Door::class.java)
            viewModelScope.launch(Dispatchers.Main) {
                door.value  = d.status
            }
        }
    }
    private fun getFanStatus(){
        viewModelScope.launch {
            fan.value = ApiConnect.service!!.getFan().body()?.status == true

        }
        socket.on("fanStatusUpdate"){ args ->
            val msg = args[0] as JSONObject
            val f = Gson().fromJson(msg.toString(), Fan::class.java)
            viewModelScope.launch(Dispatchers.Main) {
                fan.value  = f.status
            }
        }
    }
    private fun getLedAt(){
        viewModelScope.launch {
            light1.value = ApiConnect.service!!.getLedAt("living room").body()?.status == true
            light2.value = ApiConnect.service!!.getLedAt("bedroom").body()?.status == true

        }
        socket.on("ledStatusUpdate"){ args ->
            val msg = args[0] as JSONObject
            val light = Gson().fromJson(msg.toString(), Led::class.java)
            viewModelScope.launch(Dispatchers.Main) {
                if(light.location == "living room"){
                    light1.value  = light.status
                }else{
                    light2.value  = light.status
                }
            }
        }
    }

    private fun getTemperatureAndHumidity(){
        socket.on("temperatureHumidityUpdate") { args ->
            val msg = args[0] as JSONObject
            val tmp = Gson().fromJson(msg.toString(), TempAndHumid::class.java)
            viewModelScope.launch(Dispatchers.Main) {
                temp = if((tmp.temperature - tmp.temperature.toInt()).toDouble() == 0.0) tmp.temperature.toInt().toString()
                else tmp.temperature.toString()
                humid = if((tmp.humidity - tmp.humidity.toInt()).toDouble() == 0.0) tmp.humidity.toInt().toString()
                else tmp.humidity.toString()
            }
        }
    }
    private fun getPumpStatus(){
        viewModelScope.launch {
            pump.value = ApiConnect.service!!.getPump().body()?.status == true

        }
        socket.on("pumpStatusUpdate"){ args ->
            val msg = args[0] as JSONObject
            val f = Gson().fromJson(msg.toString(), Pump::class.java)
            viewModelScope.launch(Dispatchers.Main) {
                pump.value  = f.status
            }
        }
    }

    private fun getFsStatus() {
        viewModelScope.launch {
            fs.value = ApiConnect.service?.getFlameSensor()?.body()?.status == true

        }
        socket.on("fsStatusUpdate") { args ->
            val msg = args[0] as JSONObject
            val data = Gson().fromJson(msg.toString(), FlameSensor::class.java)
            viewModelScope.launch(Dispatchers.Main) {
                fs.value  = data.status
            }
        }
    }
    private fun getRsStatus() {
        viewModelScope.launch {
            rs.value = ApiConnect.service?.getRainSensor()?.body()?.status == true

        }
        socket.on("rsStatusUpdate") { args ->
            val msg = args[0] as JSONObject
            val data = Gson().fromJson(msg.toString(), RainSensor::class.java)
            viewModelScope.launch(Dispatchers.Main) {
                rs.value  = data.status
            }
        }
    }

    private fun getGsStatus() {
        viewModelScope.launch {
            gs.value = ApiConnect.service?.getGasSensor()?.body()?.status == true

        }
        socket.on("gsStatusUpdate") { args ->
            val msg = args[0] as JSONObject
            val data = Gson().fromJson(msg.toString(), GasSensor::class.java)
            viewModelScope.launch(Dispatchers.Main) {
                gs.value  = data.status
            }
        }
    }

    val deviceList : MutableList<General> = arrayListOf(
        General(
            R.drawable.bulb,"Đèn",
            "Phòng khách",Color.Yellow,
            Color.Black,
            Color.Red.copy(alpha = 0.3f),
            Color.Black.copy(0.1f),
            light1
        ) {
            updateLightAtLivingRoom(it)
        },
        General(
            R.drawable.bulb,"Đèn",
            "Phòng ngủ",Color.Yellow,
            Color.Black,
            Color.Red.copy(alpha = 0.3f),
            Color.Black.copy(0.1f),
            light2
        ) { updateLightAtBedRoom(it) },
        General(R.drawable.pump,"Máy bơm",
            null,Color.Green,
            Color.Black,
            Color.Red.copy(alpha = 0.3f),
            Color.Black.copy(0.1f),
            pump

        ) { updatePump(it) },
        General(R.drawable.fan,"Quạt",
            null,Color.Blue,
            Color.Black,
            Color.Red.copy(alpha = 0.3f),
            Color.Black.copy(0.1f),
            fan

        ) { updateFan(it) },
        General(R.drawable.left_open,"Cửa",
            null, Brown,
            Color.Black,
            Color.Red.copy(alpha = 0.3f),
            Color.Black.copy(0.1f),
            door

        ) { updateDoor(it) },
    )

    val sensorList : MutableList<General> = arrayListOf(
        General(R.drawable.flamesen,"Cảm biến lửa",
            null,Color.DarkGray,
            Color.Black,
            Color.Red.copy(alpha = 0.3f),
            Color.Black.copy(0.1f),
            fs

        ) { updateFsStatus(it) },
        General(R.drawable.gassen,"Cảm biến khói",
            null,Color.Yellow,
            Color.Black,
            Color.Red.copy(alpha = 0.3f),
            Color.Black.copy(0.1f),
            gs
        ) { updateGsStatus(it) },
        General(R.drawable.rain,"Cảm biến mưa",
            null,Color.Blue,
            Color.Black,
            Color.Red.copy(alpha = 0.3f),
            Color.Black.copy(0.1f),
            rs

        ) { updateRsStatus(it) },
    )

}
