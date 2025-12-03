package com.example.myhome.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.domain.Data
import com.example.myhome.domain.FlameSensor
import com.example.myhome.domain.GasSensor
import com.example.myhome.domain.RainSensor
import com.example.myhome.domain.response.Model
import com.example.myhome.domain.response.Result
import com.example.myhome.network.ApiConnect
import com.example.myhome.service.SocketHandler
import com.google.gson.Gson
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class RainViewmodel : ViewModel() {
    private var socket: Socket = SocketHandler.getSocket()

    val _rainSensor = MutableStateFlow<com.example.myhome.domain.response.Result>(com.example.myhome.domain.response.Result.Nothing)
    val rainSensor : StateFlow<com.example.myhome.domain.response.Result> = _rainSensor


    private val _switchStatus = MutableSharedFlow<com.example.myhome.domain.response.Result>()
    val switchStatus = _switchStatus

    private val _sendStatus = MutableSharedFlow<com.example.myhome.domain.response.Result>()
    val sendStatus = _sendStatus

    var status = mutableStateOf(false)
        private set

    var list = mutableStateOf(listOf<Data>())
        private set

    var value = mutableStateOf("")
        private set

    var info = mutableStateOf("")
        private set


    init {
        socket.on(Socket.EVENT_CONNECT) {
            Log.d("DUCLUONG", "Connected to NestJS")
        }
        viewModelScope.launch {

            _rainSensor.emit(com.example.myhome.domain.response.Result.Loading)
            try {
                val result = ApiConnect.service!!.getRainSensor()
                val res = com.example.myhome.domain.response.Result.Response<Response<RainSensor>>(result)
                status.value = result.body()?.status == true
                value.value = result.body()?.level.toString()
                list.value = result.body()?.data ?: emptyList()
                info.value = result.body()?.infor ?: ""
                _rainSensor.emit(res)
            }catch (e: Exception){
                _rainSensor.emit(com.example.myhome.domain.response.Result.Error)
                Log.d("DUCLUONG", "Connected")

            }
        }
        socket.on("fsStatusUpdate"){
                args ->
            val msg = args[0] as JSONObject
            val data = Gson().fromJson(msg.toString(), RainSensor::class.java)
            viewModelScope.launch(Dispatchers.Main) {
                status.value  = data.status
                value.value = data.level.toString()
            }
        }
    }
    fun updateRsStatus(s: Boolean) {
        viewModelScope.launch {
            _switchStatus.emit(com.example.myhome.domain.response.Result.Loading)
            try {
                val result = ApiConnect.service!!.updateRs(RainSensor(status = s))
                val res = com.example.myhome.domain.response.Result.Response<Response<Model>>(result)
                _switchStatus.emit(res)
                res.t?.body()?.apply {
                    if (success) {
                        status.value  = s
                    }
                }
            } catch (e: Exception) {
                _switchStatus.emit(com.example.myhome.domain.response.Result.Error)
            }
        }
    }
    fun updateRsLevel(s: Int) {
        viewModelScope.launch {
            _sendStatus.emit(com.example.myhome.domain.response.Result.Loading)
            try {
                val result = ApiConnect.service!!.updateRsLevel(RainSensor(status=status.value,level = s))
                val res = com.example.myhome.domain.response.Result.Response<Response<Model>>(result)
                _sendStatus.emit(res)
                res.t?.body()?.apply {
                    if (success) {
                        value.value  = s.toString()
                    }
                }
            } catch (e: Exception) {
                _sendStatus.emit(Result.Error)
            }
        }
    }
}