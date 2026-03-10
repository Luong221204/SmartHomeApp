package com.example.myhome.graph

import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.myhome.R
import com.example.myhome.compose.house.AddDeviceOrSensorDialog
import com.example.myhome.compose.house.RoomCard
import com.example.myhome.compose.skeleton.ShimmerDeviceListItem
import com.example.myhome.compose.templates.DoubleInRow
import com.example.myhome.domain.home.Room
import com.example.myhome.network.api.Staff
import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.view.DeviceActivity
import com.example.myhome.view.SensorActivity
import com.example.myhome.viewmodel.MainEvent
import com.example.myhome.viewmodel.MainViewmodel
import com.example.myhome.viewmodel.Resource

@Composable
fun RoomDetailScreen(
    navController: NavController,
    modifier: Modifier,
    room: Room,
    viewmodel: MainViewmodel
) {
    val roomDetailState = viewmodel.mapRoom[room.id]?.collectAsState()?.value
    LaunchedEffect(roomDetailState) {
        Log.d("DUCLUONG", "RoomDetailScreen $roomDetailState")
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val addNewState = viewmodel.addNewState.collectAsState(Resource.Idle)
    BackHandler() {
        navController.popBackStack()
        viewmodel.switchScreen(MainEvent.LeaveRoomEvent(room.id?:""))
    }
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(
            key = "title",
            span = {
                GridItemSpan(maxLineSpan)
            }) {
            RowTitle(room.name,{
                navController.popBackStack()
                viewmodel.switchScreen(MainEvent.LeaveRoomEvent(room.id?:""))
            }) {
                showDialog = true
            }
        }

        when (val r = roomDetailState) {
            is Resource.Loading -> {
                item(span = {
                    GridItemSpan(maxLineSpan)
                }) {
                    ShimmerDeviceListItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        isLoading = true
                    ) {}
                }
            }

            is Resource.Success -> {
                items(
                    r.data.size,
                    key = {
                        r.data[it].id ?: 0
                    },
                ) {
                    FanControlCard(r.data[it], { status ->
                        viewmodel.updateHardware(listOf(r.data[it].copy(status = status)))
                    },
                        { v ->
                            if (v.kind == "DEVICE") {
                                val intent = Intent(context, DeviceActivity::class.java)
                                intent.putExtra("device",v)
                                context.startActivity(intent)
                            }else{
                                val intent = Intent(context, SensorActivity::class.java)
                                intent.putExtra("sensor",v)
                                context.startActivity(intent)
                            }
                        }) { r ->
                        viewmodel.deleteHardware(room.id ?: "", r)
                    }
                }
            }

            else -> {}
        }

    }
    Loading(addNewState.value)

    if (showDialog) {
        AddDeviceOrSensorDialog({
            showDialog = false
        }) { n, t, k ->
            viewmodel.addNewDeviceOrSensor(n, t, k, room.id ?: "")
        }
    }
}

@Composable
fun RowTitle(
    text: String,
    onBack:()->Unit,
    onClickAdd: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxSize()
    ) {
        val (back, name, add) = createRefs()
        Icon(
            painter = painterResource(R.drawable.back),
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    onBack()
                }
                .size(24.dp)
                .constrainAs(back) {
                    top.linkTo(name.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(name.bottom)
                }
        )
        Text(
            text = text,
            style = AppTheme.typography.deviceLargeTitle,
            modifier = Modifier.constrainAs(name) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Icon(
            painter = painterResource(R.drawable.add),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .constrainAs(add) {
                    top.linkTo(name.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(name.bottom)
                }
                .clickable {
                    onClickAdd()
                }
        )
    }
}