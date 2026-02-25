package com.example.myhome.compose.templates

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.example.myhome.R
import com.example.myhome.compose.Switch
import com.example.myhome.domain.device.SafetyLevel
import com.example.myhome.domain.device.TimeDto
import com.example.myhome.domain.sensor.Data
import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.ui.theme.Purple80
import java.time.format.TextStyle

@Composable
fun SensorHeader(modifier: Modifier,s: Sensor,onSwitch:(Boolean)->Unit){
    ConstraintLayout(
        modifier = modifier
    ) {
        var checked by remember {
            mutableStateOf(s.status)
        }
        val (icon,sensor,status) = createRefs()
        AsyncImage(
            model = s.icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp).constrainAs(icon){
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            }
        )
        s.name?.let {
            Text(
                text = it,
                style = AppTheme.typography.additionTitle,
                modifier = Modifier.constrainAs(sensor){
                    start.linkTo(icon.end, margin = 15.dp)
                    top.linkTo(icon.top)
                    bottom.linkTo(icon.bottom)
                }
            )
        }
        androidx.compose.material3.Switch(
           checked = checked == true,
            onCheckedChange = {
                checked = it
                onSwitch(it)
            },
            modifier = Modifier.constrainAs(status){
                end.linkTo(parent.end)
                top.linkTo(icon.top)
                bottom.linkTo(icon.bottom)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color.Green,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.Gray
            )
        )
    }
}

data class Sensor(
    val id:String?=null,
    val name:String?=null,
    val icon: String?=null,
    val status: Boolean?=null,
    val threshold: SensorThreshold?=null,
    val sensorData:List<SensorData>?=null,
    val data:Map<String,List<Data>>?=null,
    val safetyLevels:List<SafetyLevel>?=null,


    )

data class SensorThreshold(
    val min: Float,
    val max: Float,
    val current: Float
)