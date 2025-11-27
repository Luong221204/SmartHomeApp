package com.example.myhome.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.Constants
import com.example.myhome.domain.Door
import com.example.myhome.domain.Fan
import com.example.myhome.domain.FlameSensor
import com.example.myhome.domain.GasSensor
import com.example.myhome.domain.Led
import com.example.myhome.domain.Pump
import com.example.myhome.domain.RainSensor
import com.example.myhome.domain.response.Model
import com.example.myhome.domain.response.Result
import com.example.myhome.domain.voice.Requires
import com.example.myhome.network.ApiConnect

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

import kotlinx.serialization.*
import retrofit2.Response

class VoiceViewmodel : ViewModel(){
    var text by mutableStateOf("Xin chào")
    private val _response = MutableSharedFlow<Result>()
    val response: SharedFlow<Result> = _response

    fun sendMessage(message: String) {
        val jsonSchema = Schema.obj(
            mapOf("requires" to Schema.array(
                Schema.obj(
                    mapOf(
                        "stuff" to Schema.enumeration(
                            Constants.list
                        ),
                        "status" to Schema.boolean()
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
            _response.emit(Result.Loading)
            try {
                val response = model.generateContent(message)
                response.text?.let { it ->
                    val data = Gson().fromJson(it, Requires::class.java)
                    val result = Result.Response<Response<Model>>(null)
                    data.requires.forEach {
                        if(it.stuff == "đèn phòng ngủ"){
                            ApiConnect.service?.updateLedAt(Led(status = it.status, location = "living room"))?.let { t->
                                result.t = t
                            }
                        }else if(it.stuff == "đèn phòng khách"){
                            ApiConnect.service?.updateLedAt(Led(status = it.status, location = "bedroom"))?.let { t->
                                result.t = t
                            }
                        }else if(it.stuff == "máy bơm"){
                            ApiConnect.service?.updatePump(Pump(it.status))?.let { t->
                                result.t = t
                            }
                        }else if(it.stuff == "quạt"){
                            ApiConnect.service?.updateFan(Fan(it.status))?.let { t->
                                result.t = t
                            }
                        }else if(it.stuff == "cửa"){
                            ApiConnect.service?.updateDoor(Door(it.status))?.let { t->
                                result.t = t
                            }
                        }else if(it.stuff == "cảm biến lửa"){
                            ApiConnect.service?.updateFs(FlameSensor(it.status))?.let { t->
                                result.t = t
                            }
                        }else if(it.stuff == "cảm biến khói"){
                            ApiConnect.service?.updateGs(GasSensor(it.status))?.let { t->
                                result.t = t
                            }
                        }else if(it.stuff == "cảm biến mưa"){
                            ApiConnect.service?.updateRs(RainSensor(it.status))?.let { t->
                                result.t = t
                            }
                        }
                    }
                    _response.emit(result)
                }
            } catch (e: Exception) {
                _response.emit(Result.Error)
            }
        }
    }
    fun updateText(s:String){
        text = s
    }

}