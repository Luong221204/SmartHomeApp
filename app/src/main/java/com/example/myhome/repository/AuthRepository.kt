package com.example.myhome.repository

import com.example.myhome.domain.User
import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.network.FcmToken

interface AuthRepository {
    suspend fun login(user: User): NetworkResult<User>
    suspend fun register(user: User): NetworkResult<User>
    suspend fun forgot(user: User): NetworkResult<User>
    suspend fun resetAfterForgot(user: User): NetworkResult<User>
    suspend fun refreshToken(user: User): NetworkResult<User>
    suspend fun updateFcmToken(fcmToken: FcmToken): NetworkResult<User>
    suspend fun deleteFcmToken(fcmToken: FcmToken): NetworkResult<User>
}
