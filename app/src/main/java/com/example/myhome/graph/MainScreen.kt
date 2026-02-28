package com.example.myhome.graph

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import com.example.myhome.R

import com.example.myhome.compose.house.RoomCard
import com.example.myhome.compose.skeleton.ShimmerDeviceListItem
import com.example.myhome.compose.skeleton.shimmerEffect
import com.example.myhome.compose.templates.DoubleInRow
import com.example.myhome.domain.device.TimeDto
import com.example.myhome.domain.home.House
import com.example.myhome.domain.home.Room
import com.example.myhome.domain.sensor.Data

import com.example.myhome.viewmodel.HouseUiState
import com.example.myhome.viewmodel.Resource
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier,
    houseState: StateFlow<HouseUiState>,
){
    Column(modifier = modifier.fillMaxSize()) {
        HouseBannerWithFab(houseState.value.houseInfoState) { }
        Spacer(modifier = Modifier.height(32.dp))
        ShimmerDeviceListItem(
            modifier = Modifier.padding(horizontal = 16.dp),
            isLoading = houseState.value.listRoomState is Resource.Loading
        ){
            when(val r = houseState.value.listRoomState){
                is Resource.Error->{
                    Box(modifier = Modifier.fillMaxSize()){
                        Image(
                            painter = painterResource(R.drawable.fail),
                            contentDescription = null,
                            modifier = Modifier.width(200.dp).height(150.dp).align(Alignment.Center)
                        )
                    }
                }
                is Resource.Success<List<Room>> -> {
                    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)){
                        r.data.DoubleInRow {
                                first,second->
                            RoomCard(first)
                            second?.let {
                                RoomCard(it)
                            }
                        }
                    }

                }
                else->{}
            }
        }
    }
}
fun toTimeDto(dateTime: LocalDateTime): TimeDto {
    val zoned = dateTime.atZone(ZoneId.systemDefault())
    val instant = zoned.toInstant()
    return TimeDto(
        _seconds = instant.epochSecond,
        _nanoseconds = instant.nano.toLong()
    )
}
fun generateData(
    start: LocalDateTime,
    min: Float,
    max: Float
): List<Data> {

    return (0 until 24).map { hour ->
        val time = start.plusHours(hour.toLong())

        Data(
            level = Random.nextFloat() * (max - min) + min,
            time = toTimeDto(time)
        )
    }
}
fun houseInfoConstraints(): ConstraintSet {
    return ConstraintSet {
        val houseName = createRefFor("house_name")
        val address = createRefFor("address")
        val totalPower = createRefFor("total_power")
        val statsRow = createRefFor("stats_row")

        // Tên nhà và địa chỉ bên trái
        constrain(houseName) {
            top.linkTo(parent.top, margin = 20.dp)
            start.linkTo(parent.start, margin = 24.dp)
        }
        constrain(address) {
            top.linkTo(houseName.bottom, margin = 4.dp)
            start.linkTo(houseName.start)
        }

        // Công suất tổng bên phải (Số to nhất)
        constrain(totalPower) {
            top.linkTo(houseName.top)
            end.linkTo(parent.end, margin = 24.dp)
            bottom.linkTo(address.bottom)
        }

        // Hàng thông số bên dưới (Phòng & Thiết bị)
        constrain(statsRow) {
            top.linkTo(address.bottom, margin = 32.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom, margin = 20.dp)
            width = Dimension.fillToConstraints
        }
    }
}
@Composable
fun HouseWeatherCard(
    houseInfoState: Resource<House>
) {
    Log.d("DUCLUONG", "HouseWeatherCard: $houseInfoState")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF23263A)) // Màu xanh đen như ảnh
    ) {
        ConstraintLayout(
            constraintSet = houseInfoConstraints(),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            if(houseInfoState is Resource.Loading){
                Box(modifier = Modifier
                    .layoutId("house_name")
                    .width(200.dp)
                    .height(24.dp)
                    .shimmerEffect()
                    .clip(RoundedCornerShape(4.dp))
                )
                Box(modifier = Modifier
                    .layoutId("address")
                    .width(130.dp)
                    .height(18.dp)
                    .shimmerEffect()
                    .clip(RoundedCornerShape(4.dp))
                )
                Box(modifier = Modifier
                    .layoutId("total_power")
                    .width(50.dp)
                    .height(50.dp)
                    .shimmerEffect()
                    .clip(RoundedCornerShape(4.dp))
                )
                Box(modifier = Modifier
                    .layoutId("stats_row")
                    .fillMaxWidth(0.5f)
                    .height(24.dp)
                    .shimmerEffect()
                    .clip(RoundedCornerShape(4.dp))
                )


            }else if(houseInfoState is Resource.Success){
                // 1. House Name
                Text(
                    text = houseInfoState.data.name?:"",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.layoutId("house_name")
                )

                // 2. Address
                Text(
                    text = houseInfoState.data.address?:"",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    modifier = Modifier.layoutId("address")
                )

                // 3. Total Power (Số 22 trong ảnh)
                Row(modifier = Modifier.layoutId("total_power")) {
                    Text(
                        text = "${houseInfoState.data.totalPower}",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Light
                        )
                    )
                    Text(
                        text = "W", // Đơn vị công suất
                        style = TextStyle(color = Color.White, fontSize = 18.sp),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // 4. Stats Row (Hàng thông số dưới cùng)
                Row(
                    modifier = Modifier.layoutId("stats_row"),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoItem(label = "Phòng", value = houseInfoState.data.totalRoom.toString())
                    InfoItem(label = "Thiết bị", value = houseInfoState.data.totalDevice.toString())
                    InfoItem(label = "Cảm biến", value = houseInfoState.data.totalSensor.toString()) // Giữ lại cho giống mẫu
                }
            }

        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = TextStyle(color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            text = label,
            style = TextStyle(color = Color.Gray, fontSize = 12.sp)
        )
    }
}
@Composable
fun HouseBannerWithFab(
    houseInfoState: Resource<House>,
    onFabClick: () -> Unit,
) {
    Log.d("DUCLUONG", "HouseBannerWithFab:")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 28.dp)
    ) {

        HouseWeatherCard(
            houseInfoState = houseInfoState
        )
        if(houseInfoState is Resource.Success){
            FloatingActionButton(
                onClick = onFabClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter) // Căn về góc dưới bên phải
                    .offset(y = 12.dp),         // Đẩy nút xuống dưới để "đè" lên mép banner
                shape = CircleShape,
                containerColor = Color(0xFFFF9800), // Màu cam nổi bật
                contentColor = Color(0xFFF2F2F2),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 10.dp
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = "Add Device",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

    }
}