package com.example.myhome.compose.templates

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myhome.R
import com.example.myhome.domain.device.SafetyLevel

@Composable
fun TemperatureAlertDialog(
    title:String,
    icon:Int,
    value: Float,
    level: Legend,
    time: String,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onCheck: () -> Unit = {}
) {

    val infiniteTransition = rememberInfiniteTransition(label = "")

    // Nhấp nháy nhẹ nếu nguy hiểm
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (level.meaning == "CRITICAL") 0.5f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Dialog(onDismissRequest = onDismiss) {

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E1E)
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                // HEADER
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.material3.Icon(
                            painter = painterResource(icon),
                            contentDescription = null,
                            tint = level.color,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        androidx.compose.material3.Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color.Gray.copy(alpha = 0.3f)
                )

                // TIME
                Text(
                    text = "Thời gian: $time",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(16.dp))

                // VALUE
                Text(
                    text = "${value.toInt()}°C",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = level.color,
                    modifier = Modifier.alpha(animatedAlpha)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Mức độ: ${level.meaning.uppercase()}",
                    fontSize = 14.sp,
                    color = level.color
                )

                Spacer(Modifier.height(28.dp))

                // BUTTONS
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedButton(
                        onClick = onCheck,
                        modifier = Modifier.width(120.dp).height(40.dp).align(Alignment.Center),
                        border = BorderStroke(1.dp, level.color)
                    ) {
                        Text("XÁC NHẬN", color = level.color)
                    }
                }
            }
        }
    }
}

