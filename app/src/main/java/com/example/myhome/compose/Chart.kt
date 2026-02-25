package com.example.myhome.compose

import android.util.Log
import androidx.compose.foundation.background
import com.example.myhome.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.Gravity
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.myhome.domain.sensor.Data
import com.example.myhome.ui.theme.AppTheme
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot

@Composable
fun ChartScreen(list: List<Data>?) {
    // list an toàn
    val safeList = list.orEmpty()
    if (safeList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .height(AppTheme.dimen.chart),
            contentAlignment = Alignment.Center
        ) {
            Text("Chưa có dữ liệu",style = AppTheme.typography.placeHolder,color = AppTheme.color.policyColor)
        }
        return
    }


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

            .labelAndAxisLinePadding(20.dp)
            .startPadding(20.dp)
            .axisPosition(Gravity.RIGHT)
            .build()
    }

    // --------------------------
    // 3. Y-axis (chỉ dựa trên steps, Vico tự scale)
    val steps = 2
    val yAxisData = remember {
        AxisData.Builder()
            .steps(steps)
            .backgroundColor(Color.White)
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
                            lineType = LineType.Straight(isDotted = false),
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
            backgroundColor = Color.White
        )
    }

    // --------------------------
    // 5. Hiển thị biểu đồ
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .background(color =  Color.White)
            .height(AppTheme.dimen.chart),
        lineChartData = lineChartData
    )
}

@Composable
fun SampleLineGraph(lines: List<List<DataPoint>>) {
    LineGraph(
        plot = LinePlot(
            listOf(
                LinePlot.Line(
                    lines[0],
                    LinePlot.Connection(color = Color.Blue),
                    LinePlot.Intersection(color = Color.Red),
                    LinePlot.Highlight(color = Color.Yellow),
                )
            ),
            grid = LinePlot.Grid( Color.Red, steps = 4),
        ),
        modifier = Modifier.fillMaxWidth().height(200.dp),
        onSelection = { xLine, points ->
            // Do whatever you want here
        }
    )
}
@Composable
fun BottomBar(navController:NavHostController){
    var selectedItem by remember { mutableStateOf("Home") }
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    NavigationBar(
        modifier = Modifier.height(100.dp)
    ) {
        EnumIcon.entries.forEach {it->
            NavigationBarItem(
                selected =currentDestination?.hierarchy?.any { v->

                    v.route == it.name
                }==true ,
                onClick = {
                    selectedItem = it.name
                    navController.navigate(it.name){
                        popUpTo("Home"){
                            saveState=true
                        }
                        launchSingleTop=true
                        restoreState = true
                    }
                },
                alwaysShowLabel = it.name == selectedItem,
                modifier = Modifier.size(60.dp),
                label = {
                    Text(it.name, style = AppTheme.typography.policyTitle)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = it.icon),
                        contentDescription = it.name,
                        modifier = Modifier.size(AppTheme.dimen.thumbSize)
                    )
                }
            )
        }
    }
}
enum class EnumIcon(
    val icon:Int,
    val selectedColor: Color,
    val unSelectedColor: Color
){
    Home(R.drawable.home, Color.Red,Color.Gray),
    Chart(R.drawable.log_out,Color.Red,Color.Gray),
    Account(R.drawable.ic_user,Color.Red,Color.Gray)
}