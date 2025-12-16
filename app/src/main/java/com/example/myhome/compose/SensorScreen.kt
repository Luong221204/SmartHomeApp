package com.example.myhome.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhome.R
import com.example.myhome.domain.device.Data
import com.example.myhome.domain.response.Result
import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.ui.theme.Purple40
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SensorScreen(
    modifier: Modifier,
    nameSensor: String,
    nameChart: String,
    maxLevel:Int,
    minLevel: Int,
    status: StateFlow<com.example.myhome.domain.response.Result>,
    sendStatus:SharedFlow<com.example.myhome.domain.response.Result>,
    level: MutableState<String>,
    list: MutableState<List<Data>>,
    isOpen: MutableState<Boolean>,
    info: MutableState<String>,
    onSwitch: (Boolean) -> Unit,
    onSend:(Int)->Unit,
    back:()->Unit
) {
    var isReadonly by remember { mutableStateOf(true) }
    var value by remember { mutableStateOf("") }
    val displayValue = if (isReadonly) level.value.toString() else value
    var isLoading by remember { mutableStateOf(false) }
    var isSendLoading by remember { mutableStateOf(false) }
    val sensor = status.collectAsState()
    val send = sendStatus.collectAsState(initial = com.example.myhome.domain.response.Result.Nothing)
    when(send.value){
        is Result.Loading -> { isSendLoading = true }
        is Result.Response<*> -> { isSendLoading = false}
        is Result.Error -> {}
        is com.example.myhome.domain.response.Result.Nothing -> {}
    }
    when (sensor.value) {
        is com.example.myhome.domain.response.Result.Loading -> { isLoading = true }
        is com.example.myhome.domain.response.Result.Response<*> -> { isLoading = false }
        is com.example.myhome.domain.response.Result.Error -> {}
        is Result.Nothing -> {}
    }
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.dimen.heightLargeButton)
                    .background(color = AppTheme.color.backgroundAppColor)
                    .padding(start = AppTheme.padding.smallHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier
                        .size(AppTheme.dimen.iconLargeSize)
                        .clickable {
                            back()
                        }
                )
                Spacer(Modifier.width(AppTheme.spacer.heightDash))
                Text(
                    text = nameSensor,
                    style =AppTheme.typography.introSectionTitle
                )
            }
            Spacer(Modifier.width(AppTheme.spacer.heightDash))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppTheme.dimen.heightLargeButton)
                        .background(color = Color.White)
                        .padding(horizontal = AppTheme.padding.smallHorizontalPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Trạng thái",
                        style = AppTheme.typography.introSectionTitle
                    )
                    Switch(isOpen) {
                        onSwitch(it)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppTheme.dimen.heightLargeButton)
                        .padding(horizontal = AppTheme.padding.smallHorizontalPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = info.value,
                        style = AppTheme.typography.placeHolder,
                        color = AppTheme.color.policyColor
                    )
                    Box(modifier = Modifier){
                        if (isSendLoading) CircularProgressIndicator(
                            modifier = Modifier.size(AppTheme.dimen.iconLargeSize),
                            strokeWidth = AppTheme.dimen.strokeWidth,
                            color = AppTheme.color.policyColor
                        )
                        else{
                            Text(
                                if (isReadonly) "Chỉnh sửa" else "Gửi",
                                style =AppTheme.typography.additionTitle,
                                color = Color.Magenta,
                                modifier = Modifier.clickable {
                                    if (isReadonly) {
                                        isReadonly = false
                                    } else {
                                        onSend(value.toInt())
                                        isReadonly = true
                                    }
                                })
                        }
                    }

                }
                OutlinedTextField(
                    value = displayValue,
                    onValueChange = {
                        value = it
                    },
                    readOnly = isReadonly,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppTheme.padding.smallHorizontalPadding),
                    supportingText = {
                        val intValue = value.toIntOrNull()
                        if (!isReadonly) {
                            if (intValue == null) {
                                Column {
                                    Text(
                                        text = "Vui lòng nhập số",
                                        color = Color.Red
                                    )
                                    Spacer(modifier = Modifier.height(AppTheme.spacer.smallGap))
                                }
                            } else if (intValue !in minLevel..maxLevel) {
                                Column {
                                    Text(
                                        text = "Giá trị phải nằm trong khoảng từ $minLevel đến $maxLevel",
                                        color = Color.Red
                                    )
                                    Spacer(modifier = Modifier.height(AppTheme.spacer.smallGap))
                                }
                            } else {
                                Column {
                                    Text(
                                        text = "Giá trị hợp lệ",
                                        color = Color.Green
                                    )
                                    Spacer(modifier = Modifier.height(AppTheme.spacer.smallGap))
                                }
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
                Spacer(modifier = Modifier.height(AppTheme.spacer.heightDash))
                Text(
                    text = nameChart,
                    style = AppTheme.typography.introSectionTitle,
                    modifier = Modifier.padding(start = AppTheme.padding.smallHorizontalPadding)
                )
                ChartScreen(list = list.value)
            }
        }

    }
}

