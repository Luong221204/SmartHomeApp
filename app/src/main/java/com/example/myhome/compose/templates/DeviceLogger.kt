package com.example.myhome.compose.templates

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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

@Composable
fun SmartFeaturesSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tính năng thông minh",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 1. Card Hẹn giờ đếm ngược
        SmartFeatureCard(
            title = "Hẹn giờ tắt",
            description = "Tự động tắt sau 30 phút nữa",
            icon = R.drawable.timer,
            trailing = {
                Switch(checked = true, onCheckedChange = {})
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Card Liên kết cảm biến (Automation)
        SmartFeatureCard(
            title = "Tự động hóa theo nhiệt độ",
            description = "Bật quạt khi nhiệt độ > 30°C",
            icon = R.drawable.auto,
            trailing = {
                // Nút cấu hình nhanh
                IconButton(onClick = { /* Mở màn hình cài đặt ngưỡng */ }) {
                    Icon(painter = painterResource(R.drawable.back), contentDescription = null, modifier = Modifier.size(24.dp))
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Nút thêm kịch bản mới
        OutlinedButton(
            onClick = { /* Thêm kịch bản */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(painter = painterResource(R.drawable.add), contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Thêm kịch bản tự động")
        }
    }
}

@Composable
fun SmartFeatureCard(
    title: String,
    description: String,
    icon: Int,
    trailing: @Composable () -> Unit
) {
    // Sử dụng Outlined Card để giống phong cách ô số "20" và "50" trong ảnh của ông
    Card(
        modifier = Modifier.fillMaxWidth(),
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
fun ActivityLogSection() {
    val logs = listOf(
        ActivityLog("22:55", "Bạn đã bật thiết bị", LogType.MANUAL),
        ActivityLog("22:50", "Tự động bật (Nhiệt độ > 30°C)", LogType.AUTO),
        ActivityLog("18:00", "Tự động tắt theo lịch trình", LogType.AUTO),
        ActivityLog("12:30", "Mất kết nối Wifi", LogType.SYSTEM)
    )

    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text(
            text = "Lịch sử hoạt động",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Card bao quanh toàn bộ danh sách để đồng bộ với ảnh của ông
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.outlinedCardColors(),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                logs.forEachIndexed { index, log ->
                    ActivityLogItem(
                        log = log,
                        isLast = index == logs.size - 1
                    )
                }

                // Nút xem thêm
                TextButton(
                    onClick = { /* Mở màn hình log chi tiết */ },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Xem tất cả nhật ký", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun ActivityLogItem(log: ActivityLog, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Cột 1: Vẽ đường Timeline
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Chấm tròn đầu dòng
            val dotColor = when(log.type) {
                LogType.AUTO -> Color(0xFF4CAF50) // Xanh lá cho tự động
                LogType.MANUAL -> Color(0xFF2196F3) // Xanh dương cho thủ công
                LogType.SYSTEM -> Color(0xFFF44336) // Đỏ cho hệ thống
            }

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(dotColor, CircleShape)
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

        // Cột 2: Thông tin thời gian và hành động
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = log.time,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Text(
                text = log.action,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (log.type == LogType.AUTO) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
data class ActivityLog(
    val time: String,
    val action: String,
    val type: LogType
)

enum class LogType {
    AUTO,   // Hệ thống tự làm (do sensor kích hoạt)
    MANUAL, // Người dùng bấm nút
    SYSTEM  // Lỗi hoặc cảnh báo hệ thống
}