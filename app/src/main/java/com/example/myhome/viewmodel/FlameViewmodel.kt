package com.example.myhome.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.domain.device.Data
import com.example.myhome.domain.device.FlameSensor
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

class FlameViewmodel : ViewModel() {
    private var socket: Socket = SocketHandler.getSocket()

    val _flameSensor = MutableStateFlow<Result>(com.example.myhome.domain.response.Result.Nothing)
    val flameSensor : StateFlow<com.example.myhome.domain.response.Result> = _flameSensor


    private val _switchStatus = MutableSharedFlow<Result>()
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

            _flameSensor.emit(com.example.myhome.domain.response.Result.Loading)
            try {
                val result = ApiConnect.service!!.getFlameSensor()
                val res = com.example.myhome.domain.response.Result.Response<Response<FlameSensor>>(result)
                status.value = result.body()?.status == true
                value.value = result.body()?.level.toString()
                list.value = result.body()?.data ?: emptyList()
                info.value = result.body()?.infor ?: ""
                _flameSensor.emit(res)
            }catch (e: Exception){
                _flameSensor.emit(Result.Error())
                Log.d("DUCLUONG", "Connected")

            }
        }
        socket.on("fsStatusUpdate"){
                args ->
            val msg = args[0] as JSONObject
            val data = Gson().fromJson(msg.toString(), FlameSensor::class.java)
            viewModelScope.launch(Dispatchers.Main) {
                status.value  = data.status
                value.value = data.level.toString()
                list.value = data.data
            }
        }
    }
    fun updateFsStatus(s: Boolean) {
        viewModelScope.launch {
            _switchStatus.emit(com.example.myhome.domain.response.Result.Loading)
            try {
                val result = ApiConnect.service!!.updateFs(FlameSensor(status = s))
                val res = com.example.myhome.domain.response.Result.Response<Response<Model>>(result)
                _switchStatus.emit(res)
                res.t?.body()?.apply {
                    if (success) {
                        status.value  = s
                    }
                }
            } catch (e: Exception) {
                _switchStatus.emit(com.example.myhome.domain.response.Result.Error())
            }
        }
    }
    fun updateFsLevel(s: Int) {
        viewModelScope.launch {
            _sendStatus.emit(com.example.myhome.domain.response.Result.Loading)
            try {
                val result = ApiConnect.service!!.updateFsLevel(FlameSensor(status=status.value,level = s))
                val res = com.example.myhome.domain.response.Result.Response<Response<Model>>(result)
                _sendStatus.emit(res)
                res.t?.body()?.apply {
                    if (success) {
                        value.value  = s.toString()
                    }
                }
            } catch (e: Exception) {
                _sendStatus.emit(Result.Error())
            }
        }
    }
}