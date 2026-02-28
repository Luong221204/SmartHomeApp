package com.example.myhome.repository

import com.example.myhome.domain.automation.Automation
import com.example.myhome.domain.response.NetworkResult

interface AutomationRepository {

    suspend fun getAutomationByDeviceId(deviceId:String,limit:Int,startAfter:String?=null): NetworkResult<List<Automation>>

    suspend fun createAutomation(automation: Automation): NetworkResult<Boolean>

}