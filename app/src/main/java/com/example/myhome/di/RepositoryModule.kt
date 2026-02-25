package com.example.myhome.di

import com.example.myhome.repoimpl.AuthRepoImpl
import com.example.myhome.repoimpl.AutomationRepoImpl
import com.example.myhome.repoimpl.DeviceRepoImpl
import com.example.myhome.repoimpl.HouseRepoImpl
import com.example.myhome.repoimpl.SensorRepoImpl
import com.example.myhome.repository.AuthRepository
import com.example.myhome.repository.AutomationRepository
import com.example.myhome.repository.DeviceRepository
import com.example.myhome.repository.HouseRepository
import com.example.myhome.repository.SensorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepoImpl): AuthRepository

}

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule1 {

    @Binds
    @ViewModelScoped
    abstract fun bindDeviceRepository(deviceRepositoryImpl: DeviceRepoImpl): DeviceRepository


    @Binds
    @ViewModelScoped
    abstract fun bindAutomationRepository(automationRepositoryImpl: AutomationRepoImpl): AutomationRepository


    @Binds
    @ViewModelScoped
    abstract fun bindHouseRepository(houseImpl: HouseRepoImpl): HouseRepository

    @Binds
    @ViewModelScoped
    abstract fun bindSensorRepository(sensorRepositoryImpl: SensorRepoImpl): SensorRepository
}