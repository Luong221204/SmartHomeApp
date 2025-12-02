package com.example.myhome.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.myhome.view.MainActivity
import com.example.myhome.MyApplication
import com.example.myhome.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: "ESP32 Update"
        val body = remoteMessage.notification?.body ?: ""
        showNotification(title, body)
    }

    @SuppressLint("FullScreenIntentPolicy")
    private fun showNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }


        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        var icon = BitmapFactory.decodeResource(resources, R.drawable.error)
        if(body == "cháy"){
            icon = BitmapFactory.decodeResource(resources, R.drawable.fire)
        }else if(body == "khói"){
            icon = BitmapFactory.decodeResource(resources, R.drawable.dust)
        }


        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, MyApplication.Companion.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setLargeIcon(icon)
            .setSmallIcon(if(body == "cháy") R.drawable.fire else R.drawable.dust)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setFullScreenIntent(pendingIntent, true)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()

        notificationManager.notify(1001, notification)
           }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAlarmActivity(title:String?, body: String?){
        val intent = Intent(this, AlarmForegroundService::class.java)
        intent.putExtra("title", title?: "Cảnh báo")
        intent.putExtra("body", body?: "Có cảnh báo mới!")
        Log.d("DUCLUONG","onSend")
        startForegroundService(intent)
    }
}