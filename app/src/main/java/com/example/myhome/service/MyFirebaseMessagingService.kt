package com.example.myhome.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.myhome.view.MainActivity
import com.example.myhome.MyApplication
import com.example.myhome.R
import com.example.myhome.local.DataManager
import com.example.myhome.local.DataManager2
import com.example.myhome.network.ApiConnect
import com.example.myhome.network.FcmToken
import com.example.myhome.receiver.NotificationReceiver
import com.example.myhome.repoimpl.AuthRepoImpl
import com.example.myhome.repository.AuthRepository
import com.example.myhome.view.TempAndHumidityActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var dataManager: DataManager2
    @Inject lateinit var authRepo: AuthRepository
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            if(dataManager.isLoggedIn()){
                handleDataMessage(remoteMessage)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if(dataManager.isLoggedIn()){
            CoroutineScope(Dispatchers.IO).launch {
                dataManager.saveFcmToken(token)
                authRepo.updateFcmToken(FcmToken(token, dataManager.getUser().id))
            }
        }
    }

    private fun handleDataMessage(remoteMessage: RemoteMessage) {
        val type = remoteMessage.data["type"]
        when (type) {
            "JOIN_HOUSE_REQUEST" -> {
                val address = remoteMessage.data["address"] ?: ""
                val description = remoteMessage.data["description"] ?: ""
                val title = remoteMessage.notification?.title ?: "ESP32 Update"
                val body = remoteMessage.notification?.body ?: ""
                openJoinHouseRequestScreen(title, body)
            }
        }
    }

    private fun openJoinHouseRequestScreen(title: String, body: String) {
        val okIntent = Intent(this, NotificationReceiver::class.java).apply {
            action = "REQUEST_JOIN_HOUSE"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val okPending = PendingIntent.getBroadcast(
            this,
            0,
            okIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val intent = Intent(this, TempAndHumidityActivity::class.java).apply {
        }

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, MyApplication.Companion.HOUSE_ACCESS_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(body)
            .addAction(R.drawable.img, "Đồng ý",okPending)
            .addAction(R.drawable.close, "Từ chối",null)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(1002, notification)
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