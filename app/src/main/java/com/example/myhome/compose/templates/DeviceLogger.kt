package com.example.myhome.compose.templates

import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myhome.R
import com.example.myhome.domain.device.ActivityLog
import com.example.myhome.util.convertToColorForLog
import com.example.myhome.util.toDateMonthYear

@Composable
fun SmartFeaturesButtons(
    onScheduleClick:()-> Unit,
    onAutomationClick:()-> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            onClick = { onScheduleClick() },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(painter = painterResource(R.drawable.add), contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Thêm kịch bản ")
        }
        Spacer(modifier = Modifier.width(16.dp))
        OutlinedButton(
            onClick = { onAutomationClick() },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(painter = painterResource(R.drawable.timer), contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Hẹn giờ")
        }
    }
}

@Composable
fun SmartFeatureCard(
    title: String,
    description: String,
    icon: Int,
    trailing: @Composable () -> Unit,
    onClick:()->Unit
) {
    // Sử dụng Outlined Card để giống phong cách ô số "20" và "50" trong ảnh của ông
    Card(
        modifier = Modifier.fillMaxWidth().clickable{
            onClick()
        },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF0F0F0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource( icon), contentDescription = null, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            trailing()
        }
    }
}



@Composable
fun ActivityLogItem(log: ActivityLog, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Cột 1: Vẽ đường Timeline
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(log.type.convertToColorForLog(), CircleShape)
            )

            // Đường kẻ dọc (nếu không phải item cuối cùng)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(Color(0xFFE0E0E0))
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = log.time.toDateMonthYear(),
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = log.description,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
data class ActivityLog(
    val time: String,
    val description: String,
    val type: String
){
    val map = mapOf(
        "AUTO" to Color(0xFF4CAF50),
        "MANUAL" to Color(0xFF2196F3),
        "SYSTEM" to Color(0xFFF44336)
    )
    fun convertToColor():Color{
        return map[type]?:Color(0xFF4CAF50)
    }
}

