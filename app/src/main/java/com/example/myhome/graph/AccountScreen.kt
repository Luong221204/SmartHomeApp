package com.example.myhome.graph

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.myhome.compose.house.AddRoomDialog

@Composable
fun AccountScreen(){
    Box(modifier = Modifier.fillMaxSize().background(color = Color.Red)){
        AddRoomDialog({}) {
            t1,t2->
        }
    }
}