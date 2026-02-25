package com.example.myhome.compose.device

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhome.R
import com.example.myhome.ui.theme.Pink40
import com.example.myhome.view.ExpandableSensorCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun AutomationCreatorScreen() {
    // Các State tạm thời để demo giao diện
    var selectedSensor by remember { mutableStateOf("Nhiệt độ") }
    var operator by remember { mutableStateOf(">") }
    var threshold by remember { mutableStateOf(30f) }
    var actionDevice by remember { mutableStateOf("Máy bơm") }
    var actionStatus by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Tạo kịch bản mới", style = MaterialTheme.typography.headlineMedium)

        // --- KHỐI ĐIỀU KIỆN (IF) ---
        AutomationCard(title = "NẾU (IF)", color = Color(0xFFE3F2FD)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Khi cảm biến:", style = MaterialTheme.typography.labelMedium)
                // Dropdown chọn cảm biến (Giả lập bằng Row)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(onClick = { /* Mở menu chọn */ }) { Text(selectedSensor) }
                    OutlinedButton(onClick = { /* Mở menu chọn */ }) { Text(operator) }
                }

                Text("Giá trị ngưỡng: ${threshold.toInt()}", style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = threshold,
                    onValueChange = { threshold = it },
                    valueRange = 0f..100f
                )
            }
        }

        Icon(painter = painterResource(R.drawable.add), contentDescription = null,
            modifier = Modifier.align(Alignment.CenterHorizontally).size(24.dp), tint = Color.Gray)

        // --- KHỐI HÀNH ĐỘNG (THEN) ---
        AutomationCard(title = "THÌ (THEN)", color = Color(0xFFF1F8E9)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Thực hiện hành động:", style = MaterialTheme.typography.labelMedium)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(R.drawable.auto), modifier = Modifier.size(24.dp),contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(actionDevice, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.weight(1f))
                    // Nút chuyển trạng thái
                    Switch(checked = actionStatus, onCheckedChange = { actionStatus = it })
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- NÚT LƯU ---
        Button(
            onClick = { /* Gửi data lên NestJS Repo */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Lưu kịch bản")
        }
    }
}

@Composable
fun AutomationCard(title: String, color: Color, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceItem(
    name: String,
    icon: Int,
    isSelected: Boolean,
    onSelect: (Boolean) -> Unit
) {
    Surface(
        onClick = {
            onSelect(isSelected)
        },
        modifier = Modifier.size(90.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.White,
        border = BorderStroke(
            2.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)
        ),
        tonalElevation = 4.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun DeviceSelectorRow(
    title: String,
    devices: List<Pair<String, Int>>,
    listState: List<Boolean>,
    onDeviceSelected: (Int, Boolean) -> Unit
) {
    Log.d("DUCLUONG", "ProfessionalAutomationScreen: $listState")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = Color.Gray)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            itemsIndexed(devices){
                index,(name, icon) ->
                DeviceItem(
                    name = name,
                    icon = icon,
                    isSelected = listState[index],
                    onSelect = {
                        onDeviceSelected(index,it)
                    }
                )

            }

        }
    }
}
data class SelectState(
    val list:List<Boolean> = listOf(false,false,false),
    val int :Int?=null
)
@Composable
fun ProfessionalAutomationScreen() {
    var selectedSensor by remember { mutableStateOf("Nhiệt độ") }
    var selectedActuator by remember { mutableStateOf("Máy bơm") }

    val sensorList = listOf("Nhiệt độ" to R.drawable.temperature, "Độ ẩm" to R.drawable.humidity, "Ánh sáng" to R.drawable.bulb)
    val actuatorList = listOf("Máy bơm" to R.drawable.pump, "Đèn" to R.drawable.bulb, "Quạt" to R.drawable.fan)
    var isExpanded by remember { mutableStateOf(false) }
    var state = remember {
        mutableStateListOf(false,false,false)
    }
    Log.d("DUCLUONG", "ProfessionalAutomationScreen: $state")
    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Tự động hóa", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)

        // KHỐI IF - CHỌN ĐIỀU KIỆN
        Card(
            modifier = Modifier.fillMaxWidth()
                .animateContentSize( // Tự động làm mượt khi kích thước Card thay đổi
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                ).clickable{
                    isExpanded = !isExpanded
                },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFF)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("NẾU (IF)", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                DeviceSelectorRow(
                    title = "Chọn cảm biến đầu vào:",
                    devices = sensorList,
                    listState = state,
                    onDeviceSelected = { index, isSelected ->
                        val list = mutableStateListOf(false, false, false)
                        list[index] = !isSelected
                        state=list
                    },
                )
            }
            // --- PHẦN CHI TIẾT (Trượt xuống khi isExpanded = true) ---
            AnimatedVisibility(
                visible = state[0],
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                ExpandableSensorCard("Độ ẩm", R.drawable.humidity, "%", Color(0xFF2196F3))
            }
        }

    }
}


@Composable
fun DetailSettingSection(
    selectedSensor: String ="",
    threshold: Float = 0f,
    onThresholdChange: (Float) -> Unit={},
    operator: String="=",
    onOperatorChange: (String) -> Unit = {},
    actionStatus: Boolean=true,
    onStatusChange: (Boolean) -> Unit={}
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

        // --- PHẦN THIẾT LẬP NGƯỠNG (CHO PHẦN IF) ---
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text("Thiết lập ngưỡng", style = MaterialTheme.typography.titleSmall)
                // Hiển thị con số cực to làm điểm nhấn thẩm mỹ
                Text(
                    text = "${threshold.toInt()}${if (selectedSensor == "Nhiệt độ") "°C" else "%"}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Slider thiết kế hiện đại
            Slider(
                value = threshold,
                onValueChange = onThresholdChange,
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )

            // Chọn toán tử: > , < , =
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(">", "<", "=").forEach { op ->
                    val isSelected = operator == op
                    OutlinedIconToggleButton(
                        checked = isSelected,
                        onCheckedChange = { onOperatorChange(op) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = IconButtonDefaults.outlinedIconToggleButtonColors(
                            checkedContainerColor = MaterialTheme.colorScheme.primary,
                            checkedContentColor = Color.White
                        )
                    ) {
                        Text(op, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Divider(color = Color.LightGray.copy(alpha = 0.3f))

        // --- PHẦN THIẾT LẬP HÀNH ĐỘNG (CHO PHẦN THEN) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Trạng thái thiết bị:", style = MaterialTheme.typography.titleSmall)

            // Nút gạt Bật/Tắt thiết kế dạng Chip cho sang
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.2f))
                    .padding(4.dp)
            ) {
                val statusOptions = listOf(true to "BẬT", false to "TẮT")
                statusOptions.forEach { (status, label) ->
                    val isSelected = actionStatus == status
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFF4CAF50) else Color.Transparent)
                            .clickable { onStatusChange(status) }
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else Color.Gray,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}