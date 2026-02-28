package com.example.myhome.network.api

import com.example.myhome.domain.automation.Automation
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AutomationService {

    @GET("automation")
    suspend fun getAutomationByDeviceId(@Query("deviceId") deviceId:String,@Query("limit") limit:Int,@Query("startAfter") startAfter:String?=null):List<Automation>

    @POST("automation/create")
    suspend fun createAutomation(@Body() automation: Automation):Boolean
}