package com.example.myhome.di

import com.example.myhome.network.api.DeviceService
import com.example.myhome.network.api.AuthService
import com.example.myhome.network.api.AutomationService
import com.example.myhome.network.api.HouseService
import com.example.myhome.network.api.SensorService
import com.example.myhome.network.auth.AuthInterceptor2
import com.example.myhome.network.auth.TokenAuthenticator2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun getOkHttpClient(authInterceptor: AuthInterceptor2, tokenAuthenticator: TokenAuthenticator2): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun getRetrofit(okHttpClient: OkHttpClient): Retrofit{
        return  Retrofit.Builder()
            .baseUrl("http://192.168.1.17:5435/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun getAuthService(
        retrofit: Retrofit
    ): AuthService{
        return retrofit.create(AuthService::class.java)
    }


    @Provides
    @Singleton
    fun getDeviceService(
        retrofit: Retrofit
    ): DeviceService {
        return retrofit.create(DeviceService::class.java)
    }

    @Provides
    @Singleton
    fun getAutomationService(retrofit: Retrofit): AutomationService{
        return retrofit.create(AutomationService::class.java)
    }

    @Provides
    @Singleton
    fun getHouseService(retrofit: Retrofit): HouseService{
        return retrofit.create(HouseService::class.java)
    }

    @Provides
    @Singleton
    fun getSensorService(retrofit: Retrofit): SensorService{
        return retrofit.create(SensorService::class.java)
    }
}