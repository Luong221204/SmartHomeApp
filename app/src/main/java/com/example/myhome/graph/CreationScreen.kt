package com.example.myhome.graph

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.myhome.R
import com.example.myhome.compose.house.AddDeviceOrSensorDialog
import com.example.myhome.compose.house.RoomCard
import com.example.myhome.compose.skeleton.ShimmerDeviceListItem
import com.example.myhome.compose.templates.DoubleInRow
import com.example.myhome.domain.home.Room
import com.example.myhome.network.api.Staff
import com.example.myhome.viewmodel.MainViewmodel
import com.example.myhome.viewmodel.Resource

@Composable
fun RoomDetailScreen(
    modifier: Modifier,
    room: Room,
    viewmodel: MainViewmodel
){
    val roomDetailState = viewmodel.mapRoom[room.id]?.collectAsState()?.value
    var showDialog by remember {
        mutableStateOf(false)
    }
    val addNewState = viewmodel.addNewState.collectAsState(Resource.Idle)

    Box(modifier = Modifier.fillMaxSize()){
        ConstraintLayout(
            modifier = Modifier.padding(end = 16.dp,start =16.dp, top = 16.dp).fillMaxSize()
        ) {
            val (back,name,add,content) = createRefs()
            Icon(
                painter = painterResource(R.drawable.back),
                contentDescription = null,
                modifier= Modifier.size(24.dp).constrainAs(back){
                    top.linkTo(name.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(name.bottom)
                }
            )
            Text(
                text = room.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.constrainAs(name){
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
            Icon(
                painter = painterResource(R.drawable.add),
                contentDescription = null,
                modifier= Modifier.size(24.dp).constrainAs(add){
                    top.linkTo(name.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(name.bottom)
                }.clickable {
                    showDialog = true
                }
            )
            ShimmerDeviceListItem(
                modifier = Modifier.constrainAs(content){
                    top.linkTo(name.bottom,32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }.fillMaxSize(),
                isLoading = roomDetailState is Resource.Loading
            ){
                when(roomDetailState){
                    is Resource.Success<List<Staff>> -> {
                        Column(modifier = it,
                            verticalArrangement = Arrangement.spacedBy(16.dp)){
                            roomDetailState.data.DoubleInRow {
                                    first,second->
                                FanControlCard(first)
                                second?.let {
                                    FanControlCard(second)
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
        Loading(addNewState.value)
    }

    if(showDialog){
        AddDeviceOrSensorDialog({
            showDialog =false
        }){
                n,t,k->
            viewmodel.addNewDeviceOrSensor(n,t,k,room.id?:"")
        }
    }
}