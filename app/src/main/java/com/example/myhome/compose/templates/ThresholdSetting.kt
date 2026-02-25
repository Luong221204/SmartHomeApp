package com.example.myhome.compose.templates

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.ui.theme.Purple80
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun ThresholdSetting(threshold: SensorThreshold,modifier: Modifier,onValueFinished:(Float)->Unit){
    ConstraintLayout(modifier = modifier) {
        val (name,slider,min,max,current)=createRefs()
        val state = rememberSliderState(
            value = 100f*(threshold.current/threshold.max),
            valueRange = 0f..100f,
            steps = 9,
            onValueChangeFinished = {
            }
        )
        LaunchedEffect(Unit)  {
            snapshotFlow { state.value }
                .debounce(500)
                .collect {
                    onValueFinished(threshold.max * state.value / 100)
                }
        }
        Text(
            text = "Thiết lập ngưỡng:",
            style = AppTheme.typography.deviceLargeTitle,
            modifier = Modifier.constrainAs(name){
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )
        Text(
            text = "${threshold.min.toInt()}",
            style = AppTheme.typography.additionTitle,
            modifier = Modifier.constrainAs(min){
                top.linkTo(slider.bottom, margin = 16.dp)
                start.linkTo(slider.start)
            }
        )
        Text(
            text = "${threshold.max.toInt()}",
            style = AppTheme.typography.additionTitle,
            modifier = Modifier.constrainAs(max){
                top.linkTo(slider.bottom, margin = 16.dp)
                end.linkTo(slider.end)
            }
        )
        Box(modifier = Modifier.constrainAs(current){
            top.linkTo(name.top)
            bottom.linkTo(name.bottom)
            end.linkTo(parent.end)
        }.border(width = 1.dp,color=Color.Black, shape = RoundedCornerShape(7.dp)).height(35.dp).width(60.dp)){
            Text(
                text = "${(state.value/100*threshold.max).toInt()}",
                style = AppTheme.typography.additionTitle,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Slider(
            state = state,
            modifier = Modifier.fillMaxWidth().height(10.dp).constrainAs(slider){
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(current.bottom, margin = 32.dp)
            },
            colors = SliderDefaults.colors(
                thumbColor = Purple80,
                activeTrackColor = Purple80,
                inactiveTrackColor = Purple80
            ),
            thumb = {
            },
            track = {
                SliderDefaults.Track(
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    sliderState = state,
                    thumbTrackGapSize = 1.dp,
                    trackInsideCornerSize=5.dp
                )
            }
        )
    }

}