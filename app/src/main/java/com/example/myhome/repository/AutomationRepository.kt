package com.example.myhome.repository

import com.example.myhome.domain.automation.Automation
import com.example.myhome.domain.response.NetworkResult

interface AutomationRepository {

    suspend fun getAutomationByDeviceId(deviceId:String): NetworkResult<List<Automation>>
}