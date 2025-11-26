package com.example.myhome.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myhome.MainActivity
import com.example.myhome.MyApplication
import com.example.myhome.R

class AlarmForegroundService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val title = intent?.getStringExtra("title") ?: "Cảnh báo"
        val body = intent?.getStringExtra("body") ?: "Thiết bị gửi cảnh báo!"
        startAlarmNotification(title, body)

        return START_NOT_STICKY
    }
    @SuppressLint("FullScreenIntentPolicy", "ForegroundServiceType")
    private fun startAlarmNotification(title: String, body: String) {


        val fullScreenIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Notification
        val notification = NotificationCompat.Builder(this, MyApplication.Companion.ALARM_ID)
            .setSmallIcon(R.drawable.fire)
            .setContentTitle(title+"haha")
            .setContentText(body+"haha")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)   // giống alarm
            .build()
        Log.d("DUCLUONG","onSHow")

        startForeground(8888, notification)
    }
}