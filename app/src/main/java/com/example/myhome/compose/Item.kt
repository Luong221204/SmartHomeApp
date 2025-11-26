package com.example.myhome.compose

import android.bluetooth.BluetoothClass
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.stylusHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhome.MainViewmodel
import com.example.myhome.Result
import com.example.myhome.Switch
import kotlin.math.roundToInt

@Composable
fun Device(
    checked: MutableState<Boolean>,
    modifier: Modifier,
    iconId :Int,
    name:String,
    addition:String?,
    selectedColor: Color,
    unSelectedColor: Color = Color.Black,
    background:Color = Color.Red.copy(0.3f),
    unSelectedBack:Color =Color.Black.copy(0.1f),
    onSwitch:(Boolean)->Unit
){
    Column(
        modifier = modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .background(color =if(checked.value) background else unSelectedBack ),
    ) {
        Spacer(modifier= Modifier.height(10.dp))
        Row(
            modifier= Modifier.fillMaxWidth().height(36.dp),
            horizontalArrangement = Arrangement.spacedBy(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = null,
                modifier= Modifier.size(48.dp),
                tint = if(checked.value) selectedColor else unSelectedColor
            )

            Switch(checked){
                onSwitch(it)
            }
        }
        Spacer(modifier= Modifier.height(10.dp))
        Box(
            modifier = Modifier.height(54.dp).padding(start = 16.dp),
        ) {
            Column(
                 modifier = Modifier.align(alignment = Alignment.Center).fillMaxWidth()
            ) {
                Text(
                    text = name,
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold)
                )
                addition?.apply {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = this,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif,
                            )
                    )
                }
            }

        }
    }
}



