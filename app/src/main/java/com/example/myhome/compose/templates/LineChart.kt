package com.example.myhome.compose.templates

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.myhome.util.Constants
import com.example.myhome.R
import com.example.myhome.domain.device.SafetyLevel
import com.example.myhome.domain.sensor.Data
import com.example.myhome.ui.theme.AppTheme
import com.google.firebase.logger.Logger
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

@Composable
fun LineChart(
    modifier: Modifier,
    data1: Map<String,List<Data>>,
    title:String,
    icon:Int,
    safetyLevels:List<SafetyLevel>?,
    maxValue:Float,
    ySteps:Int,
    onClick:(DialogData)-> Unit = {}
){
    ConstraintLayout(modifier = modifier)
     {
        val legends = mutableListOf<Legend>()
         var j = 0
         if(data1.size >1){
             data1.forEach {
                     (key,value)->
                 legends.add(Legend(Constants.chartColors[j],key))
                 j++
             }
         }
         safetyLevels?.forEach {
             legends.add(Legend(it.color,it.meaning))
         }
         val guideLine = createGuidelineFromBottom(if(legends.isNotEmpty()) 0.2f else 0f)

         val (chart,legend,name) = createRefs()
         Text(
             text = "Biểu đồ $title",
             modifier = Modifier.constrainAs(name){
                 top.linkTo(parent.top)
                 start.linkTo(parent.start)
             }.padding(start = 16.dp),
             style = AppTheme.typography.deviceLargeTitle
         )
         ScrollableLineChartWithAxis(
            modifier = Modifier.constrainAs(chart){
                top.linkTo(name.bottom, margin = 24.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(guideLine)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            },
            data1,
            "Nhiệt độ",
            R.drawable.temperature,
            safetyLevels,
            maxValue,
            ySteps,
            onClick = onClick
        )
         Column(
             modifier = Modifier.constrainAs(legend){
                 top.linkTo(guideLine, margin = 16.dp)
                 start.linkTo(parent.start)
                 bottom.linkTo(parent.bottom)
                 height= Dimension.fillToConstraints
                 end.linkTo(parent.end)
             }.fillMaxWidth(0.9f),
             verticalArrangement = Arrangement.spacedBy(16.dp)
         ) {
             legends.DoubleInRow { first, second ->
                 LegendItem(modifier = Modifier.width(130.dp), color = first.color, text = first.meaning)
                 second?.let{
                     LegendItem(modifier = Modifier.width(130.dp), color = second.color, text = second.meaning)
                 }

             }
         }


    }
}

@Composable
fun ScrollableLineChartWithAxis(
    modifier: Modifier,
    data1: Map<String,List<Data>>,
    title:String,
    icon:Int,
    safetyLevels:List<SafetyLevel>?,
    maxValue:Float,
    ySteps:Int,
    onClick:(DialogData)-> Unit = {},
) {
    lateinit var points1 : Map<String,List<Offset>>
    val scrollState = rememberScrollState()
    val textMeasurer = rememberTextMeasurer()
    val spacePerPoint = 200f
    val leftPadding = 60f
    val bottomPadding = 40f
    val topPadding = 0f
    val strokeLine = 8f
    val textStyle = TextStyle(fontSize = 10.sp, color = Color.Black)
    val legends = mutableListOf<Legend>()

    val chartWidth = leftPadding + (data1.values.randomOrNull()?.size?:3) * spacePerPoint
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
                            var j = 0
                            points1.forEach {
                                (k,v)->
                                v.forEachIndexed {
                                    index, value->
                                    val d = pointDistance(value, dis)
                                    if(d < threshold){
                                        onClick(DialogData(k,icon,
                                            data1[k]?.get(index)?.level ?: 0f,findDegree(data1[k]?.get(index)?.level ?: 0f,j,safetyLevels,),data1[k]?.get(index)?.convertTimeDay() ?: ""))
                                        return@detectTapGestures
                                    }
                                }
                                j++
                            }

                        },
                    )
                }
        ) {

            val range = (maxValue - 0f).coerceAtLeast(1f)

            val chartHeight = size.height - bottomPadding - topPadding
            // ===== MAP DATA =====
            fun mapPoints(
                data: Map<String, List<Data>>
            ): Map<String, List<Offset>> {

                return data.mapValues { (_, list) ->

                    list.mapIndexed { index, value ->

                        val percent = ((value.level - 0f) / range)
                            .coerceIn(0f, 1f)

                        val x = leftPadding + index * spacePerPoint
                        val y = size.height - bottomPadding - percent * chartHeight

                        Offset(x, y)
                    }
                }
            }

            points1 = mapPoints(data1)
            fun valueToY(value: Float): Float {
                val percent = value / range
                return size.height - bottomPadding - percent * chartHeight
            }
            safetyLevels?.forEach { level ->

                val top = valueToY(level.max)
                val bottom = valueToY(level.min)
                drawRect(
                    color = level.color.copy(alpha = 0.5f),
                    topLeft = Offset(leftPadding, top),
                    size = Size(
                        size.width - leftPadding,
                        bottom - top
                    )
                )
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
            var j = 0
            points1.forEach {
                (key,value)->
                for (i in 0 until value.lastIndex) {
                    drawLine(
                        color = Constants.chartColors[j],
                        start = value[i],
                        end = value[i + 1],
                        strokeWidth = strokeLine
                    )
                }

                j++
            }


            data1.values.randomOrNull()?.forEachIndexed { index, d ->

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
                    AnnotatedString(d.convertTimeDay()),
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


@Composable
fun LegendItem(
    modifier: Modifier,
    color: Color,
    text: String
) {
    Row(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .height(12.dp)
                .width(28.dp)
                .background(color)
        )
        Spacer(modifier = Modifier.width(28.dp))

        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.DarkGray
        )
    }
}

fun findDegree(
    value:Float,
    index:Int,
    safetyLevels: List<SafetyLevel>?,
): Legend{
    safetyLevels?.forEach {
        if(value in it.min..it.max){
            return Legend(
                color = it.color,
                meaning = it.meaning
            )
        }

    }
    return Legend(
        color = Constants.chartColors[index],
        meaning = ""
    )
}

data class Legend(
    val color :Color = Color.White,
    val meaning :String =""
)
fun distanceToSegment(p: Offset, a: Offset, b: Offset): Float {
    val ab = b - a
    val ap = p - a
    val t = (ap.x * ab.x + ap.y * ab.y) /
            (ab.x * ab.x + ab.y * ab.y)

    val clampedT = t.coerceIn(0f, 1f)
    val closest = a + ab * clampedT

    return (p - closest).getDistance()
}
fun pointDistance(p1: Offset, p2: Offset): Float{
    val x = p1.x - p2.x
    val y = p1.y - p2.y
    val d = x*x + y*y
    return kotlin.math.sqrt(d)
}

data class DialogData(
    val title:String,
    val icon:Int,
    val value: Float,
    val level: Legend,
    val time: String,
    val add:String = ""
)
