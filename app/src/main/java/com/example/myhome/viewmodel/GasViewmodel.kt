package com.example.myhome.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.domain.device.GasSensor
import com.example.myhome.domain.response.Model
import com.example.myhome.domain.response.Result
import com.example.myhome.domain.sensor.Data
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

class GasViewmodel : ViewModel() {
    private var socket: Socket = SocketHandler.getSocket()

    val _gasSensor = MutableStateFlow<Result>(Result.Nothing)
    val gasSensor : StateFlow<Result> = _gasSensor


    private val _switchStatus = MutableSharedFlow<Result>()
    val switchStatus = _switchStatus

    private val _sendStatus = MutableSharedFlow<Result>()
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

            _gasSensor.emit(Result.Loading)
            try {
                val result = ApiConnect.service!!.getGasSensor()
                val res = Result.Response<Response<GasSensor>>(result)
                status.value = result.body()?.status == true
                value.value = result.body()?.level.toString()
                list.value = result.body()?.data ?: emptyList()
                info.value = result.body()?.infor ?: ""
                _gasSensor.emit(res)
            }catch (e: Exception){
                _gasSensor.emit(Result.Error())
                Log.d("DUCLUONG", "Connected")

            }
        }
        socket.on("gasStatusUpdate"){
                args ->
            val msg = args[0] as JSONObject
            val data = Gson().fromJson(msg.toString(), GasSensor::class.java)
            Log.d("DUCLUONG","data ${data.level}")
            viewModelScope.launch(Dispatchers.Main) {
                status.value  = data.status
                value.value = data.level.toString()
                list.value = data.data
                Log.d("DUCLUONG","${data.data.size}")
            }
        }
    }
    fun updateGsStatus(s: Boolean) {
        viewModelScope.launch {
            _switchStatus.emit(Result.Loading)
            try {
                val result = ApiConnect.service!!.updateGs(GasSensor(status = s))
                val res = Result.Response<Response<Model>>(result)
                _switchStatus.emit(res)
                res.t?.body()?.apply {
                    if (success) {
                        status.value  = s
                    }
                }
                Log.d("DUCLUONG", "GS updated: ${result.body()?.success}")
            } catch (e: Exception) {
                Log.e("DUCLUONG", "GS Error: $e")
                _switchStatus.emit(Result.Error())
            }
        }
    }
    fun updateGsLevel(s: Int) {
        viewModelScope.launch {
            _sendStatus.emit(Result.Loading)
            try {
                val result = ApiConnect.service!!.updateGsLevel(GasSensor(status=status.value,level = s))
                val res = Result.Response<Response<Model>>(result)
                _sendStatus.emit(res)
                res.t?.body()?.apply {
                    if (success) {
                        value.value  = s.toString()
                    }
                }
                Log.d("DUCLUONG", "GS updated: ${result.body()?.success}")
            } catch (e: Exception) {
                Log.e("DUCLUONG", "GS Error: $e")
                _sendStatus.emit(Result.Error())
            }
        }
    }
}