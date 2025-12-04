package com.example.myhome.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.domain.device.Data
import com.example.myhome.domain.device.GasSensor
import com.example.myhome.domain.device.Humidity
import com.example.myhome.domain.device.Temperature
import com.example.myhome.domain.response.Result
import com.example.myhome.network.ApiConnect
import com.example.myhome.service.SocketHandler
import com.google.gson.Gson
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class TaHViewmodel : ViewModel() {
    private var socket: Socket = SocketHandler.getSocket()

    var list_temp = mutableStateOf(listOf<Data>())
        private set

    var list_humid = mutableStateOf(listOf<Data>())
        private set

    val _status = MutableStateFlow<Result>(Result.Nothing)
    val status : StateFlow<Result> = _status

    init {
        socket.on(Socket.EVENT_CONNECT) {
            Log.d("DUCLUONG", "Connected to NestJS")
        }
        viewModelScope.launch {
            try {
                _status.emit(Result.Loading)
                val result = ApiConnect.service!!.getTemp()
                val result2 = ApiConnect.service!!.getHumid()
                result.body()?.let {
                    list_temp.value = it.data
                }
                result2.body()?.let {
                    list_humid.value = it.data
                }
                _status.emit(Result.Response(result))
            }catch (e: Exception){
                _status.emit(Result.Error)
            }
        }
        socket.on("temperature") { args ->
            val msg = args[0] as JSONObject
            val data = Gson().fromJson(msg.toString(), Temperature::class.java)
            viewModelScope.launch() {
                list_temp.value = data.data
            }
        }
        socket.on("humidity") { args ->
            val msg = args[0] as JSONObject
            val data = Gson().fromJson(msg.toString(), Humidity::class.java)
            viewModelScope.launch() {
                list_humid.value = data.data
            }
        }
    }
}