package com.example.myhome.compose

import android.app.Dialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhome.R
import com.example.myhome.ui.theme.AppTheme

@Composable
fun InfoDialog(
    modifier: Modifier,
    isSuccess: Boolean,
    status:Boolean,
    message:String,
    onDismiss:()->Unit
) {
    Card(
        modifier = modifier.width(AppTheme.dimen.dialogSize)
            .wrapContentHeight(),
        shape = AppTheme.corner.dialogCorner,
    ) {
        Column(
            modifier = Modifier
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacer.smallGap)
        ) {
            Icon(
                painter = painterResource(if(status) R.drawable.img else R.drawable.close ), // thay bằng icon bạn có
                contentDescription = "",
                modifier = Modifier.size(AppTheme.dimen.iconHugeSize),
                tint = if(status) Color.Green else Color.Red
            )
            // Mô tả
            Text(
                text = message,
                style = AppTheme.typography.largeButtonTitle,
                color = AppTheme.color.policyColor,
                textAlign = TextAlign.Center
            )


            // Nút chính
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.color.largeButton)
            ) {
                Text(
                    text = "Ok",
                    color = AppTheme.color.textButtonColor,
                    style = AppTheme.typography.largeButtonTitle
                )
            }
        }
    }
}
