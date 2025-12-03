package com.example.myhome.compose

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.myhome.domain.Data
import com.example.myhome.domain.GasSensor

@Composable
fun ChartScreen(list: List<Data>?) {
    // list an toàn
    val safeList = list.orEmpty()
    if (safeList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Chưa có dữ liệu")
        }
        return
    }

    // --------------------------
    // 1. Tạo points
    val pointsData = remember(safeList) {
        safeList.mapIndexed { index, item ->
            Point(index.toFloat(), item.level.toFloat())
        }
    }

    // --------------------------
    // 2. X-axis
    val xAxisData = remember(safeList) {
        AxisData.Builder()
            .axisStepSize(80.dp)
            .backgroundColor(Color.Transparent)
            .steps(pointsData.size - 1)
            .labelData { i ->
                if (i in safeList.indices) safeList[i].time else ""
            }
            .labelAndAxisLinePadding(20.dp)
            .build()
    }

    // --------------------------
    // 3. Y-axis (chỉ dựa trên steps, Vico tự scale)
    val steps = 2
    val yAxisData = remember {
        AxisData.Builder()
            .steps(steps)
            .backgroundColor(Color.Transparent)
            .labelData { i -> i.toString() }
            .labelAndAxisLinePadding(20.dp)
            .build()
    }

    // --------------------------
    // 4. LineChartData
    val lineChartData = remember(pointsData) {
        LineChartData(
            linePlotData = LinePlotData(
                lines = listOf(
                    Line(
                        dataPoints = pointsData,
                        lineStyle = LineStyle(
                            lineType = LineType.Straight(isDotted = true),
                            color = Color.Green
                        ),
                        intersectionPoint = IntersectionPoint(color = Color.Green, radius = 1.dp),
                        selectionHighlightPoint = SelectionHighlightPoint(color = Color.Green),
                        shadowUnderLine = ShadowUnderLine(color = Color.Green, alpha = 0.2f),
                        selectionHighlightPopUp = SelectionHighlightPopUp(labelColor = Color.Green)
                    )
                )
            ),
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            backgroundColor = Color.Transparent
        )
    }

    // --------------------------
    // 5. Hiển thị biểu đồ
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )
}
