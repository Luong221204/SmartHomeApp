package com.example.myhome.network
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConnect {
    var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://smarthome-bohz.onrender.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var service: Service? = retrofit.create(Service::class.java)
}