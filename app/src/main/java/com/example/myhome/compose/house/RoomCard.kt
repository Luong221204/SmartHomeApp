package com.example.myhome.compose.house

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhome.R
import com.example.myhome.domain.home.Room

@Composable
fun RoomCard(
    room: Room
) {
    val icon = when(room.type) {
        "LIVING ROOM" -> R.drawable.living_room
        "BEDROOM"->R.drawable.bedroom
        "KITCHEN"->R.drawable.kitchen
        "HALLWAY"->R.drawable.hallway
        "BATHROOM"->R.drawable.bathroom
        "BASEMENT"->R.drawable.basement
        "OUTSIDE"->R.drawable.outside
        else -> {}
    }
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(180.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF23263A) // màu nền xanh đậm
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Ảnh tròn
            Image(
                painter = painterResource(id = icon as Int),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = room.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Sub text
            Text(
                text = String.format("%2d thiết bị", room.totalDevice),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
    }
}