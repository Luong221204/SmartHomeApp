package com.example.myhome.local

import android.content.Context
import android.content.SharedPreferences

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
}