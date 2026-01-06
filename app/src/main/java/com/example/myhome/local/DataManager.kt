package com.example.myhome.local

import android.content.Context
import android.content.SharedPreferences
import com.example.myhome.domain.User
import com.google.gson.Gson

object DataManager {
    var sharedPreferences: MySharedPreference? = null
    fun init(context: Context){
        sharedPreferences = MySharedPreference(context)
    }
    fun saveToken( value: String) {
        sharedPreferences?.saveToken( value)
    }
    fun getToken():String{
        return sharedPreferences?.getToken()?:""
    }

    fun saveLoginStatus(isLoggedIn: Boolean) {
        sharedPreferences?.saveLoginStatus(isLoggedIn)
    }
    fun isLoggedIn(): Boolean {
        return sharedPreferences?.isLoggedIn()?:false
    }

    fun saveRefreshToken(refreshToken: String) {
        sharedPreferences?.saveRefreshToken(refreshToken)
    }
    fun getRefreshToken(): String? {
        return sharedPreferences?.getRefreshToken()
    }

    fun saveHouseIds(houseIds: List<String>) {
        sharedPreferences?.saveHouseIds(houseIds)
    }

    fun getHouseIds(): List<String> {
        return sharedPreferences?.getHouseIds() ?: emptyList()
    }

    fun saveFcmToken(fcmToken: String) {
        sharedPreferences?.saveFcmToken(fcmToken)
    }

    fun getFcmToken(): String? {
        return sharedPreferences?.getFcmToken()
    }


    fun saveUser(user: User) {
        sharedPreferences?.saveUser(user)
    }

    fun getUser():User{
        return Gson().fromJson(sharedPreferences?.getUser(), User::class.java)
    }

}

