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
import com.example.myhome.domain.Data
import com.example.myhome.domain.response.Result
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
        is com.example.myhome.domain.response.Result.Loading -> { isSendLoading = true }
        is com.example.myhome.domain.response.Result.Response<*> -> { isSendLoading = false}
        is com.example.myhome.domain.response.Result.Error -> {}
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
                    .height(50.dp)
                    .background(color = Color.White)
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            back()
                        }
                )
                Spacer(Modifier.width(20.dp))
                Text(
                    text = nameSensor,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
            }
            Spacer(Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(color = Color.White)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Trạng thái",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                    Switch(isOpen) {
                        onSwitch(it)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = info.value,
                        style = TextStyle(fontSize = 13.sp, color = Color.Black.copy(0.8f))
                    )
                    Box(modifier = Modifier){
                        if (isSendLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        else{
                            Text(
                                if (isReadonly) "Chỉnh sửa" else "Gửi",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color = Purple40,
                                    fontWeight = FontWeight.Bold
                                ),
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
                        .padding(horizontal = 16.dp),
                    supportingText = {
                        val intValue = value.toIntOrNull()
                        if (!isReadonly) {
                            if (intValue == null) {
                                Column {
                                    Text(
                                        text = "Vui lòng nhập số",
                                        color = Color.Red
                                    )
                                    Spacer(modifier = Modifier.height(8.dp)) // khoảng cách
                                }
                            } else if (intValue !in minLevel..maxLevel) {
                                Column {
                                    Text(
                                        text = "Giá trị phải nằm trong khoảng từ $minLevel đến $maxLevel",
                                        color = Color.Red
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            } else {
                                Column {
                                    Text(
                                        text = "Giá trị hợp lệ",
                                        color = Color.Green
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = nameChart,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 16.dp)
                )

                ChartScreen(list = list.value)
            }
        }

    }
}

