package com.example.myhome.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1?.action == "REQUEST_JOIN_HOUSE"){
            Toast.makeText(p0,"haha", Toast.LENGTH_SHORT).show()
        }else if(p1?.action == "REQUEST_JOIN_HOUSEs"){
            Toast.makeText(p0,"hahas", Toast.LENGTH_SHORT).show()
        }

    }
}