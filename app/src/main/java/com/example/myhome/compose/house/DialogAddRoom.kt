package com.example.myhome.compose.house

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myhome.R
import com.example.myhome.util.Constants

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddRoomDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var roomName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Thêm phòng mới", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Ô nhập tên phòng
                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    label = { Text("Tên phòng") },
                    placeholder = { Text("Ví dụ: Phòng của Bin") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(text = "Chọn loại phòng:", style = MaterialTheme.typography.bodyMedium)

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Constants.roomName.forEach { type ->
                        FilterChip(
                            selected = (selectedType == type.key),
                            onClick = { selectedType = type.key },
                            label = { Text(type.key) },
                            leadingIcon = if (selectedType == type.key) {
                                {
                                    Icon(
                                        painter = painterResource(R.drawable.img),
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else null
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(roomName, Constants.roomName[selectedType]?:"LIVING ROOM") },
                // Chỉ cho bấm nút Lưu nếu đã nhập tên và chọn loại
                enabled = roomName.isNotBlank() && selectedType.isNotBlank()
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
fun AddDeviceOrSensorDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: String, kind: String) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("DEVICE", "SENSOR")

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") } // Loại: Đèn, Quạt / Nhiệt độ, Độ ẩm...

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm mới", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // TabLayout (TabRow)
                TabRow(
                    containerColor = Color.Transparent,
                    selectedTabIndex = selectedTabIndex
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }

                // Ô nhập tên chung
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên hiển thị") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Chọn loại dựa trên Tab đang chọn
                Text("Chọn loại ${tabs[selectedTabIndex].lowercase()}:")

                val options = if (selectedTabIndex == 0) {
                    Constants.deviceName
                } else {
                    Constants.sensorName
                }

                // Hiển thị danh sách lựa chọn nhanh (Chips)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEach { option ->
                        FilterChip(
                            selected = (type == option.key),
                            onClick = { type = option.key },
                            label = { Text(option.value) },
                            leadingIcon = if (type == option.key) {
                                {
                                    Icon(
                                        painter = painterResource(R.drawable.img),
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else null
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = name.isNotBlank() && type.isNotBlank(),
                onClick = { onConfirm(name, type, tabs[selectedTabIndex]) }
            ) {
                Text("Thêm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}