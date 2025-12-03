package com.example.myhome.network
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConnect {
    var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.122:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var service: Service? = retrofit.create(Service::class.java)
}