package com.example.myhome

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.myhome.local.DataManager
import com.example.myhome.network.ApiConnect
import com.example.myhome.service.SocketHandler

class MyApplication: Application() {
    companion object{
        const val CHANNEL_ID = "esp32_channel_id"
        const val ALARM_ID= "alarm"
    }
    override fun onCreate() {
        super.onCreate()
        createFirebaseChannel()
        createAlarmChannel()
        SocketHandler.setSocket()
        SocketHandler.connect()
        DataManager.init(applicationContext)
        ApiConnect.setToken(DataManager.getToken())
    }

    private fun createFirebaseChannel(){
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "ESP32 Notifications", NotificationManager.IMPORTANCE_HIGH)
                .apply {
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 250, 500)
                }
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun createAlarmChannel(){
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(ALARM_ID, "Alarm", NotificationManager.IMPORTANCE_HIGH)
                .apply {
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 250, 500)
                }
            notificationManager.createNotificationChannel(channel)
        }
    }
}