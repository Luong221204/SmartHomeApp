package com.example.myhome.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myhome.R
import com.example.myhome.ui.theme.AppTheme

@Preview
@Composable
fun Switch(
    modifier: Modifier = Modifier,
    checked: MutableState<Boolean>,
    switch: (Boolean) -> Unit
) {
    val switchWidth = AppTheme.dimen.switchWidth
    val switchHeight = AppTheme.dimen.switchHeight
    val thumbSize = AppTheme.dimen.thumbSize
    val padding = AppTheme.dimen.padding


// tính offset dựa trên width switch
    val thumbOffset by animateDpAsState(
        targetValue = if (checked.value) switchWidth - thumbSize - padding else 0.dp,
        label = ""
    )

    Box(
        modifier = modifier
            .width(switchWidth)
            .height(switchHeight)
            .clip(AppTheme.corner.switchCorner)
            .background(
                if (checked.value) AppTheme.color.switchOn else AppTheme.color.switchOff
            )
            .clickable { switch(!checked.value) }
            .padding(padding)
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .background(Color.White, CircleShape)
        )
    }


}

@Composable
fun DeviceSwitchCard(
    name: String,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val bgColor by animateColorAsState(if (isOn) Color(0xFFBBDEFB) else Color.White)
    val iconColor by animateColorAsState(if (isOn) Color(0xFF1976D2) else Color.Gray)

    Card(
        modifier = Modifier
            .size(150.dp)
            .clickable { onToggle(!isOn) },
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(painter = painterResource(R.drawable.bulb), contentDescription = null, tint = iconColor)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = name, fontWeight = FontWeight.Bold)
            Text(text = if (isOn) "Đang bật" else "Đang tắt", style = MaterialTheme.typography.bodySmall)
        }
    }
}