package com.example.myhome.compose.templates

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.myhome.R
import com.example.myhome.ui.theme.AppColor
import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.ui.theme.Purple80
import com.example.myhome.util.Constants
import com.example.myhome.viewmodel.SensorViewmodel
import com.google.common.graph.ValueGraphBuilder

@Composable
fun RealTimeValues(
    list: List<SensorData>,
    modifier: Modifier,
    switchState:Boolean,
    onSwitch:(Boolean)->Unit
){
    Column(modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Realtime values",
                style = AppTheme.typography.deviceLargeTitle
            )
            Switch(
                checked = switchState,
                onCheckedChange = {
                    onSwitch(!switchState)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = androidx.compose.ui.graphics.Color.White,
                    checkedTrackColor = androidx.compose.ui.graphics.Color.Green,
                    uncheckedThumbColor = androidx.compose.ui.graphics.Color.White,
                    uncheckedTrackColor = androidx.compose.ui.graphics.Color.Gray
                )
            )
        }

         list.DoubleInRowS {
             first, second, i, i2 ->
             SensorCard(
                 modifier = Modifier.width(150.dp),
                 label = first.name,
                 value = first.value,
                 color = Constants.chartColors[i],
                 unit = first.unit,
                 icon = first.icon
             )
             second?.let {
                 SensorCard(
                     modifier = Modifier.width(150.dp),
                     label = second.name,
                     value = second.value,
                     color = Constants.chartColors[i2],
                     unit = second.unit,
                     icon = second.icon
                 )
             }
         }
    }
}
@Composable
fun <T> List<T>.DoubleInRow(content: @Composable (T, T?) -> Unit){
    var i = 0

    while(i<this@DoubleInRow.size){
        val first = this@DoubleInRow[i]
        val second = this@DoubleInRow.getOrNull(i+1)
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            ) {
            content(first,second)
        }
        i += 2
    }
}
@Composable
fun <T> List<T>.DoubleInRowS(content: @Composable (T, T?, Int, Int) -> Unit){
    var i = 0


    while(i<this@DoubleInRowS.size){
        val first = this@DoubleInRowS[i]
        val second = this@DoubleInRowS.getOrNull(i+1)
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            content(first,second,i,i+1)
        }
        i += 2
    }
}
@Composable
fun Value(
    modifier: Modifier,
    name:String,
    value: String
){
    ConstraintLayout(
        modifier = modifier
    ) {
        val (nameRef,valueRef) = createRefs()
        Text(
            text = "$name :",
            modifier = Modifier.constrainAs(nameRef){
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            },
            style = AppTheme.typography.placeHolder
        )
        Box(
            modifier = Modifier.constrainAs(valueRef){
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(nameRef.bottom)
            }.height(35.dp).width(60.dp).border(
                width = 1.dp,
                color = AppColor().deviceColor,
                shape = RoundedCornerShape(7.dp)
            )
        ){
            Text(
                text = value,
                modifier = Modifier.align(Alignment.Center).padding(vertical = 5.dp),
                style = AppTheme.typography.placeHolder

            )
        }

    }
}

data class SensorData(
    val name:String,
    val value: String,
    val unit: String="",
    val icon: Int = R.drawable.humidity
)

@Composable
fun SensorCard(
    label: String,
    value: String,
    unit: String,
    icon: Int,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(painter = painterResource(icon), contentDescription = null, tint = color,modifier=Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall, color =  androidx.compose.ui.graphics.Color.Gray)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = unit,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp, start = 2.dp),
                    color = color
                )
            }
        }
    }
}