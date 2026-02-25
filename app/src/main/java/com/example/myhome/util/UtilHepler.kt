package com.example.myhome.util

import com.example.myhome.domain.response.NetworkResult
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T?
): NetworkResult<T> {
    return try {
        val result = apiCall()
        if (result != null) {
            NetworkResult.Success(result)
        } else {
            NetworkResult.Error("Response body is null")
        }
    } catch (e: HttpException) {
        NetworkResult.Error("Server error: ${e.code()}")
    } catch (e: IOException) {
        NetworkResult.Error("No internet connection")
    } catch (e: Exception) {
        NetworkResult.Error(e.message ?: "Unknown error")
    } as NetworkResult<T>
}