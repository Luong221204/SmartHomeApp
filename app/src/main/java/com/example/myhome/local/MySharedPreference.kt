package com.example.myhome.local

import android.content.Context
import androidx.core.content.edit
import com.example.myhome.domain.User
import com.google.gson.Gson

class MySharedPreference(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    fun saveToken(token: String) {
        sharedPreferences.edit { putString("token", token) }
    }
    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun saveLoginStatus(isLoggedIn: Boolean) {
        sharedPreferences.edit { putBoolean("isLoggedIn", isLoggedIn) }
    }
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun saveRefreshToken(refreshToken: String) {
        sharedPreferences.edit { putString("refreshToken", refreshToken) }
    }
    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refreshToken", null)
    }

    fun saveHouseIds(houseIds: List<String>) {
        sharedPreferences.edit {
            putStringSet("houseIds", houseIds.toSet())
        }
    }

    fun getHouseIds(): List<String> {
        return sharedPreferences.getStringSet("houseIds", emptySet())?.toList() ?: emptyList()
    }

    fun saveFcmToken(fcmToken: String) {
        sharedPreferences.edit { putString("fcmToken", fcmToken) }
    }

    fun getFcmToken(): String? {
        return sharedPreferences.getString("fcmToken", "")
    }

    fun saveUserId(userId: String) {
        sharedPreferences.edit { putString("userId", userId) }
    }

    fun getUserId(): String? {
        return sharedPreferences.getString("userId", "")
    }

    fun saveUser(user: User) {
        val userJson = Gson().toJson(user)
        sharedPreferences.edit { putString("user", userJson) }
    }

    fun getUser():String?{
        return sharedPreferences.getString("user", "")
    }

}