package com.example.myhome.local

import android.content.Context
import androidx.core.content.edit

class MySharedPreference(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    fun saveToken(token: String) {
        sharedPreferences.edit { putString("token", token) }
    }
    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }
}