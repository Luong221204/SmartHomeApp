package com.example.myhome.repoimpl

import com.example.myhome.domain.automation.Automation
import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.network.api.AutomationService
import com.example.myhome.repository.AutomationRepository
import com.example.myhome.util.safeApiCall
import javax.inject.Inject

class AutomationRepoImpl @Inject constructor(
    private val automationService: AutomationService
): AutomationRepository {
    override suspend fun getAutomationByDeviceId(deviceId: String): NetworkResult<List<Automation>> {
        return safeApiCall {
            automationService.getAutomationByDeviceId(deviceId)

        }
    }
}