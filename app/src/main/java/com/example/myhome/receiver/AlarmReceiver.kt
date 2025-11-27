package com.example.myhome.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.myhome.view.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val launchIntent = Intent(p0, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        Toast.makeText(p0,"haha", Toast.LENGTH_SHORT).show()
        try {
            p0?.startActivity(launchIntent)
        } catch (e: Exception) {
            // Xử lý lỗi (ví dụ: bị chặn trên Android 10+)
            Log.d("DUCLUONG", "Could not start Activity: ${e.message}")
            // ... Quay lại việc sử dụng Notification
        }
    }
}