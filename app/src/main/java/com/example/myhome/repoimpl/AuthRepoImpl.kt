package com.example.myhome.repoimpl

import com.example.myhome.domain.User
import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.network.FcmToken
import com.example.myhome.network.api.AuthService
import com.example.myhome.repository.AuthRepository
import com.example.myhome.util.safeApiCall
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepository {
    override suspend fun login(user: User): NetworkResult<User> {
        return safeApiCall {
            authService.login(user)
        }
    }

    override suspend fun register(user: User): NetworkResult<User> {
        return safeApiCall {
            authService.register(user)
        }
    }

    override suspend fun forgot(user: User): NetworkResult<User> {
        return safeApiCall {
            authService.forgot(user)
        }
    }

    override suspend fun resetAfterForgot(user: User): NetworkResult<User> {
        return safeApiCall {
            authService.resetAfterForgot(user)
        }
    }

    override suspend fun refreshToken(user: User): NetworkResult<User> {
        return safeApiCall {
            authService.refreshToken(user)
        }
    }

    override suspend fun updateFcmToken(fcmToken: FcmToken): NetworkResult<User> {
        return safeApiCall {
            authService.updateFcmToken(fcmToken)
        }
    }

    override suspend fun deleteFcmToken(fcmToken: FcmToken): NetworkResult<User> {
        return safeApiCall {
            authService.deleteFcmToken(fcmToken)
        }
    }

}