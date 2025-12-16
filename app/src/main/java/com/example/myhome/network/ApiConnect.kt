package com.example.myhome.network
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AuthInterceptor(private val token: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Nếu có token thì gắn vào
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}

object ApiConnect {

    private var token: String? = null

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))   // <-- interceptor gắn token
            .build()
    }

    fun setToken(newToken: String) {
        token = newToken
        // tạo lại retrofit khi token thay đổi
        retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.122:5435/")
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(Service::class.java)
    }

    var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.122:5435/")
        .client(getClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var service: Service? = retrofit.create(Service::class.java)
}
