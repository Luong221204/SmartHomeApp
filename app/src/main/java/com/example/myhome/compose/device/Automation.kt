package com.example.myhome.compose.device

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhome.R
import com.example.myhome.compose.skeleton.FullCardSkeleton
import com.example.myhome.domain.automation.Action
import com.example.myhome.domain.automation.Automation
import com.example.myhome.domain.automation.Condition
import com.example.myhome.domain.device.Device
import com.example.myhome.domain.sensor.Sensor
import com.example.myhome.ui.theme.Pink40
import com.example.myhome.util.Constants
import com.example.myhome.viewmodel.AutoSceneUiState
import com.example.myhome.viewmodel.DeviceViewmodel
import com.example.myhome.viewmodel.Resource
import kotlinx.coroutines.flow.Flow


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
    devices: List<Sensor>,
    onDeviceSelected: (Int, Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = Color.Gray)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            itemsIndexed(devices) { index, d ->
                DeviceItem(
                    name = d.name ?: "",
                    icon = Constants.deviceList[d.type?.type] ?: R.drawable.bulb,
                    isSelected = d.isSelected,
                    onSelect = {
                        onDeviceSelected(index, it)
                    }
                )

            }

        }
    }
}

data class SelectState(
    val list: List<Boolean> = listOf(false, false, false),
    val int: Int? = null
)

@Composable
fun ProfessionalAutomationScreen(
    modifier: Modifier,
    viewmodel: DeviceViewmodel,
    onSendRequest:(Automation)-> Unit,
    onBack: () -> Unit = {},
) {
    BackHandler(enabled = true) {
        onBack()
    }
    val device = viewmodel.deviceById.value.deviceState
    val automationSetupState by viewmodel.automationScene.collectAsState(Resource.Idle)
    val automationSceneState by viewmodel.automationScreen.collectAsState()
    var isShowDialog by remember {
        mutableStateOf(false)
    }
    var condition by remember {
        mutableStateOf(Condition())
    }
    var action by remember {
        mutableStateOf(Action())
    }

    if(isShowDialog){
        MyInputDialog(
            {
                isShowDialog  =false
            }
        ) {
            t1,t2->
            isShowDialog = false
            val automation = Automation(
                houseId = if(device is Resource.Success) device.data.houseId else "home1",
                name = t1,action = action.copy(command = t2),condition = condition,
                roomId = if(device is Resource.Success) device.data.roomId else "0",
                isEnabled = true,
                type = "SCHEDULE"
            )
            viewmodel.onSendAnAutomation(automation)
        }
    }
    Column(
        modifier = modifier
            .wrapContentSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (automationSceneState.isLoading) {
            FullCardSkeleton(true) { }
        } else if (automationSceneState.isSuccess) {
            Text(
                "Tự động hóa",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessHigh
                        )
                    ),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFF)),
            ) {
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DeviceSelectorRow(
                        title = "Chọn cảm biến đầu vào:",
                        devices = automationSceneState.listSensor,
                        onDeviceSelected = { index, isSelected ->
                            automationSceneState.listSensor[index].id?:""
                            viewmodel.onSwitchChange(index, isSelected)
                        },
                    )
                }
                SetupInfoForSensors(automationSceneState.listSensor,
                    if(device is Resource.Success) device.data else Device()
                ){
                    c,a->
                    condition = c
                    action = a
                    isShowDialog = true
                }
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp))



    }
    when(val r = automationSetupState){
        is Resource.Success -> {
            Toast.makeText(LocalContext.current, "Tạo thành công", Toast.LENGTH_SHORT).show()
        }
        is Resource.Error -> {
            Toast.makeText(LocalContext.current, r.message, Toast.LENGTH_SHORT).show()
        }
        is Resource.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)) // Làm mờ màn hình chính
                    .pointerInput(Unit) {}, // Ngăn chặn các sự kiện click xuyên qua lớp mờ
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Text("Đang xử lý...", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        else->{}
    }
}

@Composable
fun SetupInfoForSensors(
    sensorList: List<Sensor>,
    device: Device,
    onSetup:(Condition,Action)-> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        sensorList.forEachIndexed { index, sensor ->
            key(sensor.id) {
                AnimatedVisibility(
                    visible = sensor.isSelected,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        sensor.type?.unit?.forEach { (unitKey, unitValue) ->
                            var threshold by remember {
                                mutableFloatStateOf(0f)
                            }
                            var operator by remember {
                                mutableStateOf(">")
                            }
                            var actionStatus by remember {
                                mutableStateOf(true)
                            }
                            var valueForDevice by remember {
                                mutableFloatStateOf(0f)
                            }

                            ExpandableSensorCard(
                                name = unitKey,
                                icon = Constants.unitList[unitKey] ?: R.drawable.bulb,
                                unit = unitValue,
                                min = sensor.type.min,
                                max = sensor.type.max,
                                threshold = threshold,
                                operator = operator,
                                color = Constants.color[unitKey] ?: Color.LightGray,
                                actionStatus = actionStatus,
                                valueForDevice = valueForDevice,
                                presets = device.levels?:emptyMap(),
                                onSetupValueChange = { current ->
                                    valueForDevice = current
                                },
                                onActionChange = {
                                    actionStatus = it
                                },
                                onValueChanged = { t -> threshold = t },
                                onOperatorChange = { o -> operator = o }
                            ) {
                                val condition = Condition(
                                    operation = operator,
                                    threshold = threshold.toInt(),
                                    property = unitKey,
                                    sensorId = sensor.id
                                )
                                val action = Action(
                                    value = valueForDevice.toInt(),
                                    deviceId = device.id,
                                    status = actionStatus
                                )
                                onSetup(condition,action)
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun MyInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Nhập thông tin") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = text1,
                    onValueChange = { text1 = it },
                    label = { Text("Tên của chế độ này" , style = MaterialTheme.typography.titleSmall) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = text2,
                    onValueChange = { text2 = it },
                    label = { Text("Tên của cho thiết bị" , style = MaterialTheme.typography.titleSmall) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(text1, text2) }) {
                Text("Xác nhận")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}