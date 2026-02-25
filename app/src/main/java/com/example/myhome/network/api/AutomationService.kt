package com.example.myhome.network.api

import com.example.myhome.domain.automation.Automation
import retrofit2.http.GET
import retrofit2.http.Query

interface AutomationService {

    @GET("automation")
    suspend fun getAutomationByDeviceId(@Query("deviceId") deviceId:String):List<Automation>
}