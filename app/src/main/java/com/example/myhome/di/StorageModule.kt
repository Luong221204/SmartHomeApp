package com.example.myhome.di

import android.content.Context
import android.content.SharedPreferences
import com.example.myhome.local.MySharedPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun getSharedPreference(
        @ApplicationContext context: Context
    ) : SharedPreferences? {
        return context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }



}