package com.example.myhome.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.util.Constants
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

import retrofit2.Response

class VoiceViewmodel : ViewModel(){
    var text by mutableStateOf("Xin chào")
    private val _response = MutableSharedFlow<Result>()
    val response: SharedFlow<Result> = _response
    private val message = MutableSharedFlow<String>()
    private lateinit var job  : Job
    fun start(){
        job= viewModelScope.launch {
            Log.d("TAGS","start")
            delay(10000)
            Log.d("TAGS","complete")
        }
    }
    init {
        viewModelScope.launch {

            message.collect{
                if(!::job.isInitialized) return@collect
                if(job.isActive) {
                    Log.d("TAGS","isActive")
                    job.join()
                }
                Log.d("TAGS", it)
                /*  val response = model.generateContent(it)
                  response.text?.let { it ->
                      Log.d("TAGS", it)
                      val data = Gson().fromJson(it, Staff::class.java)
                      Log.d("TAGS","$data")
                  }*/
            }
        }
        start()
    }
    fun sendMessage2(m:String){
        viewModelScope.launch {
            message.emit(m)
        }
    }
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
                    _response.emit(result)
                }
            } catch (e: Exception) {
                _response.emit(Result.Error())
            }
        }
    }
    fun updateText(s:String){
        text = s
    }

}