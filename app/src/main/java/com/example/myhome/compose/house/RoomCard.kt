package com.example.myhome.compose.house

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.xr.compose.testing.toDp
import com.example.myhome.R
import com.example.myhome.domain.home.Room
import com.example.myhome.util.Constants

@Composable
fun RoomCard(
    room: Room,
    onClick:(Room)->Unit,
    onDelete:(Room)->Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var touchPoint by remember { mutableStateOf(Offset.Zero) }

    Card(
        modifier = Modifier
            .width(170.dp)
            .height(180.dp)
            .clickable{
                onClick(room)
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF23263A) // màu nền xanh đậm
        )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            val (icon, title, subText , erase,dropdown) = createRefs()

            Icon(
                painter = painterResource(R.drawable.three_dot),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            touchPoint = offset
                            expanded = true
                        }
                    }
                    .size(24.dp)
                    .constrainAs(erase){
                        top.linkTo(parent.top, margin = 16.dp)
                        end.linkTo(parent.end, margin = 8.dp)
                    }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {expanded = false},
                offset = DpOffset(touchPoint.x.toDp(), touchPoint.y.toDp())
            ) {
                DropdownMenuItem(text = { Text("Xóa")}, onClick = {
                    onDelete(room)
                    expanded = false
                },
                    modifier = Modifier)
            }
            //)
            // Ảnh tròn
            Image(
                painter = painterResource(id = Constants.roomList[room.type]?:R.drawable.living_room),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .constrainAs(icon){
                        top.linkTo(parent.top, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )


            // Title
            Text(
                text = room.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(title){
                    top.linkTo(icon.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )


            // Sub text
            Text(
                text = String.format("%2d thiết bị", room.totalDevice),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                modifier = Modifier.constrainAs(subText){
                    top.linkTo(title.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
        }
    }
}