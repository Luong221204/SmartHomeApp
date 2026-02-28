package com.example.myhome.view

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.example.myhome.R

import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.viewmodel.DeviceViewmodel

import dagger.hilt.android.AndroidEntryPoint
import com.example.myhome.compose.device.ButtonLoadMore
import com.example.myhome.compose.device.MyInputDialog
import com.example.myhome.compose.device.ProfessionalAutomationScreen
import com.example.myhome.compose.device.RowValue
import com.example.myhome.compose.device.TimePickerDialog
import com.example.myhome.compose.skeleton.FullCardSkeleton
import com.example.myhome.compose.skeleton.LineChartSkeleton
import com.example.myhome.compose.skeleton.LogListSkeleton
import com.example.myhome.compose.templates.ActivityLogItem
import com.example.myhome.compose.templates.CustomTopAppBar
import com.example.myhome.compose.templates.LineChartForDevice
import com.example.myhome.compose.templates.RealTimeValues
import com.example.myhome.compose.templates.SmartFeatureCard
import com.example.myhome.compose.templates.SmartFeaturesButtons
import com.example.myhome.domain.automation.Action
import com.example.myhome.domain.automation.Automation
import com.example.myhome.domain.automation.Date
import com.example.myhome.domain.automation.Schedule
import com.example.myhome.domain.device.ActivityLog
import com.example.myhome.domain.device.Device
import com.example.myhome.domain.device.EnergyStat
import com.example.myhome.domain.device.toSensorData
import com.example.myhome.util.Constants
import com.example.myhome.util.CustomNaType
import com.example.myhome.util.parseCron
import com.example.myhome.viewmodel.LoadingMoreButtonUiState
import com.example.myhome.viewmodel.NavEvent
import com.example.myhome.viewmodel.Resource
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@kotlinx.serialization.Serializable
data object DeviceRoute


@kotlinx.serialization.Serializable
data object AutomationRoute

@Serializable
data class SchedulerRoute(
    val automation: Automation,
    val device: Device
)
@AndroidEntryPoint
class DeviceActivity : BaseActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewmodel: DeviceViewmodel by viewModels()
        viewmodel.getActivityLogsInitial("FAN_1")
        viewmodel.getDetailDevice("LED_1")
        viewmodel.getEnergyStat("LED_1")
        viewmodel.getAutomationScene("FAN_1")
        setContent {
            val s = viewmodel.automationScreen.collectAsState()
            val device by viewmodel.deviceById.collectAsState()
            val logs by viewmodel.displayLogs.collectAsState()
            val automations by viewmodel.displayAutomations.collectAsState()
            val loadingMore by viewmodel.buttonLoadingForLog.collectAsState()
            val visibleCount by viewmodel.visibleCount.collectAsState()
            val navController = rememberNavController()
            val navEvent by viewmodel.navEvent.collectAsState(initial = NavEvent.Static)
            val timePickerState = rememberTimePickerState()

            LaunchedEffect(navEvent) {
                Log.d("TAG", "onCreate: $navEvent")
            }
            AppTheme {
                    NavHost(
                        navController = navController,
                        startDestination = DeviceRoute
                    ){
                        dialog<SchedulerRoute>(
                            typeMap = mapOf(
                                typeOf<Automation>() to CustomNaType.AutomationType,
                                typeOf<Device>() to CustomNaType.DeviceType
                            )
                        ){
                            val arguments = it.toRoute<SchedulerRoute>()
                            TimePickerDialog(
                                actionStatus = arguments.automation.action?.status?:false,
                                valueForDevice = arguments.automation.action?.value?.toFloat()?:0f,
                                list =arguments.device.levels?:emptyMap() ,
                                time = arguments.automation.schedule?.cron?.parseCron()?: Date(),
                                {},
                                {},
                                {}
                            )
                        }
                        composable<DeviceRoute> {
                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                topBar = {
                                    CustomTopAppBar()
                                }
                            ) { paddingValues ->
                                DeviceScreen(
                                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                                    deviceInfoState = device.deviceState,
                                    energyState = device.energyState,
                                    logs = logs,
                                    automationState = device.automationState,
                                    automations = automations,
                                    logState = device.activityState,
                                    loadingMoreButton = loadingMore,
                                    isMoreLog = loadingMore.isLogHasMore,
                                    onAddNewAutomationScene = {
                                        navController.navigate(AutomationRoute)
                                    },
                                    onAddNewScheduler = {
                                        navController.navigate(SchedulerRoute(automation = Automation(), device = it))
                                    },
                                    onMoreLogClick = {
                                        viewmodel.loadLogMore()
                                    },
                                    onAutomationItemClick = {
                                        auto,device->
                                        if(auto.type == "SCHEDULE"){
                                            navController.navigate(SchedulerRoute(auto,device))
                                        }
                                    }
                                )
                            }
                        }

                        composable<AutomationRoute>{
                            LaunchedEffect(Unit) {
                                viewmodel.nav()
                                viewmodel.moveToAutomationScreen("home1")
                            }

                            ProfessionalAutomationScreen(
                                modifier = Modifier.padding(vertical = 48.dp, horizontal = 16.dp),
                                viewmodel = viewmodel,
                                onSendRequest = {

                                }
                            ){
                                viewmodel.back()
                                navController.popBackStack()
                            }
                        }
                    }
            }
        }
    }
}
@Composable
fun DeviceScreen(
    modifier: Modifier,
    deviceInfoState:Resource<Device>,
    energyState:Resource<List<EnergyStat>>,
    automationState:Resource<List<Automation>>,
    logs:List<ActivityLog>,
    automations:List<Automation>,
    logState:Resource<List<ActivityLog>>,
    loadingMoreButton: LoadingMoreButtonUiState,
    isMoreLog:Boolean,
    onAddNewAutomationScene:(Device)->Unit,
    onAddNewScheduler: (Device)->Unit,
    onMoreLogClick:()->Unit,
    onAutomationItemClick:(Automation,Device)->Unit
){
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
    ) {
        when(deviceInfoState){
            is Resource.Success -> {
                item(
                    key = "realtime"
                ) {
                    RealTimeValues(
                        deviceInfoState.data.toSensorData(),
                        modifier = Modifier.fillMaxWidth(),
                        switchState = true,
                        {}
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item(key = "value") {
                    RowValue(
                        deviceInfoState.data.levels,
                        {
                        },
                        1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            is Resource.Loading ->{
                item(key = "realtime_skeleton") {
                    FullCardSkeleton(true) { }
                }
            }
            else->{}
        }
        when(val r = energyState) {
            is Resource.Success -> {
                item(key = "energy") {
                    LineChartForDevice(
                        modifier = Modifier.fillMaxWidth().height(350.dp),
                        data1 = r.data,
                        title = "Energy",
                        icon = 1,
                        maxValue = 100f,
                        ySteps = 10,
                        onClick = {}
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            is Resource.Loading ->{
                item(key = "energy_skeleton") {
                    LineChartSkeleton(true) { }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            else -> {}
        }
        when(automationState) {
            is Resource.Success -> {
                item(
                    key = "automation"
                ){
                    ConstraintLayout(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val (text,up,down) = createRefs()
                        Text("Tính năng tự động" , style = AppTheme.typography.deviceLargeTitle,
                            modifier = Modifier.constrainAs(text){
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                            })
                        Icon(
                            painter = painterResource(R.drawable.downward),
                            contentDescription = null,
                            modifier = Modifier.constrainAs(down){
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                end.linkTo(up.start,margin = 24.dp)
                            }.size(32.dp)
                        )
                        Icon(
                            painter = painterResource(R.drawable.up),
                            contentDescription = null,
                            modifier = Modifier.constrainAs(up){
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end)
                            }.size(24.dp)
                        )

                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                items(
                    items = automations,
                    key = { automation -> automation.id!! }
                ){
                    SmartFeatureCard(
                        title = it.name!!,
                        description = it.action!!.command!!,
                        icon = Constants.autoList[it.type!!]?: R.drawable.auto,
                        trailing = {}
                    ){

                        onAutomationItemClick(it, if(deviceInfoState is Resource.Success) deviceInfoState.data else Device())
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item(
                    key = "automation_button"
                ){
                    SmartFeaturesButtons({
                        onAddNewAutomationScene(
                            if(deviceInfoState is Resource.Success) deviceInfoState.data else Device()
                        )

                    }) {
                        onAddNewScheduler(
                            if(deviceInfoState is Resource.Success) deviceInfoState.data else Device()
                        )

                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            is Resource.Loading -> {
                item(key = "automation_skeleton") {
                    LogListSkeleton(
                        isLoading = true,
                    ) {
                    }
                }
            }
            else->{}
        }
        when(logState){
            is Resource.Success -> {
                item(key = "log_header") {
                    Text("Nhật ký hoạt động", style =  AppTheme.typography.deviceLargeTitle)
                    Spacer(modifier = Modifier.height(24.dp))
                }
                items(
                    items = logs,
                    key = { log -> log.id }
                ) { log ->
                    ActivityLogItem(log, false)
                }
                if (isMoreLog) {
                    item {
                        ButtonLoadMore(
                            isLoading = loadingMoreButton.logState is Resource.Loading,
                            onClick = {
                                onMoreLogClick()
                            }
                        )
                    }
                }
            }
            is Resource.Loading -> {
                item(key = "log_list_skeleton") {
                    LogListSkeleton(
                        isLoading = true,
                    ) {
                    }
                }

            }
            else->{}
        }
    }
}

