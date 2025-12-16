package com.example.myhome.compose

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myhome.ui.theme.AppTheme

@Preview
@Composable
fun Switch(
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
        modifier = Modifier
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

