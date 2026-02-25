package com.example.myhome.compose.templates

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.contentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.myhome.util.Constants
import com.example.myhome.R
import com.example.myhome.domain.device.SafetyLevel
import com.example.myhome.domain.sensor.Sensor
import com.example.myhome.network.ApiConnect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SensorScreen(
    sensor: com.example.myhome.compose.templates.Sensor,
    modifier: Modifier,
    switchState:Boolean,
    onSwitch:(Boolean)->Unit
){
    val co = rememberCoroutineScope()
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        var temp by remember() {
            mutableStateOf<DialogData?>(null)
        }
        sensor.sensorData?.let {
            RealTimeValues(
                list = it,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                switchState = switchState,
                onSwitch = onSwitch
            )
        }
        sensor.threshold?.let {
            Spacer(modifier = Modifier.height(32.dp))
            ThresholdSetting(
                sensor.threshold,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                {

                }
            )
        }
        sensor.data?.let{ it ->
            Spacer(modifier = Modifier.height(32.dp))
            LineChart(
                modifier = Modifier.fillMaxWidth().height(400.dp),
                it,
                sensor.name?:"",
                R.drawable.temperature,
                safetyLevels = sensor.safetyLevels,
                maxValue = sensor.threshold?.max?:100f,
                ySteps = 10,
                onClick = {i->
                    temp = i
                }
            )

            Box(modifier = Modifier.height(840.dp).fillMaxWidth())
        }
        temp?.let{
            TemperatureAlertDialog(
                title = it.title,
                icon = it.icon,
                value = it.value,
                level = it.level,
                time = it.time,
                onDismiss = {
                    temp = null
                }
            )
        }
    }
}