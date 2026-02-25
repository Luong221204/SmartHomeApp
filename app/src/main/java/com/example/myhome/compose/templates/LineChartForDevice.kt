package com.example.myhome.compose.templates

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhome.domain.device.EnergyStat
import com.example.myhome.domain.device.SafetyLevel
import com.example.myhome.domain.sensor.Data
import com.example.myhome.util.Constants
import kotlin.collections.forEach

@Composable
fun ScrollableLineChartForDeviceWithAxis(
    modifier: Modifier,
    data1: List<EnergyStat>,
    title:String,
    icon:Int,
    maxValue:Float,
    ySteps:Int,
    onClick:(DialogData)-> Unit = {},
) {
    lateinit var points1 :List<Offset>
    val scrollState = rememberScrollState()
    val textMeasurer = rememberTextMeasurer()
    val spacePerPoint = 200f
    val leftPadding = 80f
    val bottomPadding = 80f
    val topPadding = 40f
    val strokeLine = 8f
    val textStyle = TextStyle(fontSize = 10.sp, color = Color.Black)
    val legends = mutableListOf<Legend>()

    val chartWidth = leftPadding + data1.size * spacePerPoint

    Box(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
    ) {
        Canvas(
            modifier = Modifier
                .width(chartWidth.dp)
                .fillMaxHeight()
                .pointerInput(Unit){
                    detectTapGestures (
                        onTap = {
                                dis->
                            val threshold = 70f
                            points1.forEachIndexed {
                                    index, value->
                                val d = pointDistance(value, dis)
                                if(d < threshold){

                                    return@detectTapGestures
                                }
                            }

                        },
                    )
                }
        ) {

            val range = (maxValue - 0f).coerceAtLeast(1f)

            val chartHeight = size.height - bottomPadding - topPadding
            // ===== MAP DATA =====
            fun mapPoints(
                data: List<EnergyStat>
            ): List<Offset> {

                return  data.mapIndexed { index, value ->

                    val percent = ((value.kwh - 0f) / range)
                        .coerceIn(0f, 1f)

                    val x = leftPadding + index * spacePerPoint
                    val y = size.height - bottomPadding - percent * chartHeight

                    Offset(x, y)
                }
            }

            points1 = mapPoints(data1)
            fun valueToY(value: Float): Float {
                val percent = value / range
                return size.height - bottomPadding - percent * chartHeight
            }
            // ===== GRID + Y AXIS =====
            for (i in 0..ySteps) {
                if(i == 0 ) continue
                val yValue = (range / ySteps) * i
                val percent = i.toFloat() / ySteps
                val y = size.height - bottomPadding - percent * chartHeight

                // Grid line
                drawLine(
                    color = Color.Black,
                    start = Offset(leftPadding, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.5f
                )

                // Y label
                val textLayout = textMeasurer.measure(
                    AnnotatedString(yValue.toInt().toString()),
                    style = textStyle
                )

                drawText(
                    textLayout,
                    topLeft = Offset(
                        leftPadding - textLayout.size.width - 10f,
                        y - textLayout.size.height / 2f
                    )
                )
            }



            // ===== DRAW LINES =====
           for(i in 0 until points1.size-1){
               drawLine(
                   color = Color.Black,
                   start = points1[i],
                   end = points1[i+1],
               )
           }
            data1.forEachIndexed { index, d ->

                val x = leftPadding + index * spacePerPoint

                // Tick
                drawLine(
                    color = Color.Black,
                    start = Offset(x, size.height - bottomPadding),
                    end = Offset(x, size.height - bottomPadding + 10f),
                    strokeWidth = 3f
                )

                // Month text
                val textLayout = textMeasurer.measure(
                    AnnotatedString(d.date),
                    style = textStyle
                )

                drawText(
                    textLayout,
                    topLeft = Offset(
                        x - textLayout.size.width / 2f,
                        size.height - bottomPadding + 20f
                    )
                )
            }

            // ===== DRAW AXIS =====
            // Y axis
            drawLine(
                color = Color.Black,
                start = Offset(leftPadding, topPadding),
                end = Offset(leftPadding, size.height - bottomPadding),
                strokeWidth = 6f
            )

            // X axis
            drawLine(
                color = Color.Black,
                start = Offset(leftPadding, size.height - bottomPadding),
                end = Offset(size.width, size.height - bottomPadding),
                strokeWidth = 6f
            )
        }
    }
}
