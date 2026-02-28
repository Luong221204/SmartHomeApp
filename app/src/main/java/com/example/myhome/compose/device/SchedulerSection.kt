package com.example.myhome.compose.device

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.myhome.domain.automation.Date


@Composable
fun TimerScheduleCard(
    time: String, // "08:30"
    days: List<String>, // ["T2", "T4", "T6"]
    isActive: Boolean,
    actionDesc: String, // "Bật Máy bơm 50%"
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color.White else Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(if (isActive) 4.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Hiển thị Giờ lớn
                Text(
                    text = time,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) Color.Black else Color.Gray
                )

                // Hiển thị hành động
                Text(
                    text = actionDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isActive) Color(0xFF6200EE) else Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Hiển thị các ngày trong tuần
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val allDays = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                    allDays.forEach { day ->
                        val isSelected = days.contains(day)
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    if (isSelected && isActive) Color(0xFF6200EE) else Color.Transparent,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                fontSize = 10.sp,
                                color = if (isSelected && isActive) Color.White else Color.Gray,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Nút gạt On/Off cho lịch trình
            Switch(
                checked = isActive,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF4CAF50)
                )
            )
        }
    }
}

@Composable
fun AlarmStyleTimerCard(
    time: String,        // "06:00"
    note: String,        // "Tưới cây buổi sáng"
    selectedDays: List<Int>, // [2, 3, 4, 5, 6] (Thứ 2 đến thứ 6)
    isActive: Boolean,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val daysOfWeek = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { onDelete() }) // Nhấn giữ để xóa
            },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color.White else Color(0xFFF2F2F7)
        ),
        elevation = CardDefaults.cardElevation(if (isActive) 6.dp else 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    // Giờ báo thức siêu to
                    Text(
                        text = time,
                        style = TextStyle(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Light,
                            color = if (isActive) Color.Black else Color.Gray
                        )
                    )
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isActive) Color(0xFF6200EE) else Color.Gray
                    )
                }

                // Nút gạt kiểu iOS
                Switch(
                    checked = isActive,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4CAF50),
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hàng chọn thứ trong tuần
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEachIndexed { index, day ->
                    val isDaySelected = selectedDays.contains(index + 2) // T2 là 2...
                    Text(
                        text = day,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = if (isDaySelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isDaySelected && isActive) Color(0xFF6200EE) else Color.LightGray
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    actionStatus:Boolean,
    valueForDevice:Float,
    list:Map<Int,Int>,
    time: Date,
    onSetupValueChange:(Float)->Unit,
    onDismiss: () -> Unit,
    onConfirm: (TimePickerState) -> Unit,
    onActionChange:(Boolean)->Unit={}
) {
    BackHandler(true) {
        onDismiss()
    }
    val timePickerState = rememberTimePickerState(
        initialHour = time.hour?:0,
        initialMinute = time.minute?:0
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false), // Để làm dialog bo góc to
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Chọn thời gian",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Component chọn giờ của Google
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color(0xFFF8F9FA),
                        selectorColor = Color(0xFF673AB7),
                        timeSelectorSelectedContainerColor = Color(0xFFEDE7F6),
                        timeSelectorSelectedContentColor = Color(0xFF673AB7)
                    ),
                    layoutType = TimePickerLayoutType.Vertical
                )

                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessHigh
                        )
                    )
                ) {
                    ActionSwitch(actionStatus, Color(0xFF673AB7),onActionChange)// thay đổi trạng thái bat tat thiet bi
                    if(actionStatus) Spacer(modifier = Modifier.height(16.dp))
                    AnimatedVisibility(
                        visible = actionStatus,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ){
                        SetupValue(
                            list = list,
                            current = valueForDevice,
                            color = Color(0xFF673AB7),
                            onSetupValueChange = onSetupValueChange
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Hủy") }
                    Button(
                        onClick = { onConfirm(timePickerState) },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Xác nhận")
                    }
                }
            }
        }
    }
}