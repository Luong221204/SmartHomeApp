package com.example.myhome.util

import androidx.compose.ui.graphics.Color
import com.example.myhome.domain.automation.Date
import com.example.myhome.domain.device.TimeDto
import com.example.myhome.domain.response.NetworkResult
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.text.get



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

private val LOG_TIME_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")

fun TimeDto.toDateMonthYear(): String {
    val instant = Instant.ofEpochSecond(_seconds, _nanoseconds)

    return instant
        .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
        .format(LOG_TIME_FORMATTER)
}

fun String.convertToColorForLog(): Color{
    return Constants.colorForLog[this]?:Color(0xFF4CAF50)
}

fun String.parseCron(): Date {
    val parts = this.split(" ")

    if (parts.size != 6) {
        throw IllegalArgumentException("Cron phải có 6 phần")
    }
    return Date(
        hour = if(parts[2] != "*") parts[2].toInt() else null,
        minute =if(parts[1] != "*") parts[1].toInt() else null,
        second = if(parts[0] != "*") parts[0].toInt() else null,
        day = if(parts[3] != "*") parts[3].toInt() else null,
        month =if(parts[4] != "*") parts[4].toInt() else null,
        year = if(parts[5] != "*") parts[5].toInt() else null
    )
}

fun Date.toCronString(): String {
    return "${second ?: "*"} " +
            "${minute ?: "*"} " +
            "${hour ?: "*"} " +
            "${day ?: "*"} " +
            "${month ?: "*"} " +
            "${year ?: "*"}"
}