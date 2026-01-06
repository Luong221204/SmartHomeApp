package com.example.myhome.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.myhome.R
import com.example.myhome.compose.Device
import com.example.myhome.domain.User
import com.example.myhome.domain.device.General
import com.example.myhome.local.DataManager
import com.example.myhome.network.ApiConnect
import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.ui.theme.MyHomeTheme
import com.example.myhome.viewmodel.MainViewmodel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        askNotificationPermission()
        val viewmodel: MainViewmodel by viewModels()
        setContent {
            val coroutine = rememberCoroutineScope()
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize().background(color = Color.Black)) { innerPadding ->
                    MainScreen(
                        viewmodel,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppTheme.color.backgroundAppColor)
                            .padding(bottom = AppTheme.padding.paddingBar),
                        {

                            val intent = Intent(this, PasswordActivity::class.java)
                            startActivity(intent)
                        },{
                           val r = runBlocking{
                               viewmodel.logout()
                            }
                            if(r){
                                val intent = Intent(this, LoginActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                startActivity(intent)
                            }else{
                                Toast.makeText(this, "Logout failed", Toast.LENGTH_SHORT).show()
                            }
                        },
                        {
                            val intent = Intent(this, VoiceActivity::class.java)
                            startActivity(intent)
                        },
                        {
                            val intent = Intent(this, it)
                            startActivity(intent)
                        }
                    ){
                        val intent = Intent(this, TempAndHumidityActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart() }
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                FirebaseMessaging.getInstance().subscribeToTopic("esp32")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("FCM", "Subscribed to esp32 topic")
                        } else {
                            Log.e("FCM", "Subscribe failed", task.exception)
                        }
                    }
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this,getString(R.string.success_statement),Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.warning_statement),Toast.LENGTH_SHORT).show()

        }
    }
}
@Composable
fun MainScreen(viewmodel: MainViewmodel, modifier: Modifier = Modifier,onSwitch: () -> Unit,onLogout:()->Unit,onVoiceScreen:()->Unit,onNextActivity: ( Class<out Activity>) -> Unit,onTahActivity:()->Unit) {
    val temp = viewmodel.temp
    val humid = viewmodel.humid
    val isRaining = viewmodel.isRaining

// State lưu vị trí FAB
    var fabOffsetX by remember { mutableFloatStateOf(0f) }
    var fabOffsetY by remember { mutableFloatStateOf(0f) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            WeatherCard(temp, humid,isRaining,onTahActivity,onSwitch,onLogout)
            Spacer(modifier = Modifier.height(AppTheme.spacer.heightDash))
            Section("Cảm biến", viewmodel.sensorList, viewmodel,onNextActivity)
            Section("Thiết bị", viewmodel.deviceList, viewmodel,{})
        }

        // Floating button micro
        FloatingActionButton(
            onClick = { onVoiceScreen()},
            shape = CircleShape,
            containerColor = AppTheme.color.floatingButtonColor,
            modifier = Modifier
                .offset { IntOffset(fabOffsetX.roundToInt(), fabOffsetY.roundToInt()) }
                .align(Alignment.BottomEnd)
                .padding(AppTheme.padding.insidePadding)
                .size(AppTheme.dimen.fabSize)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        fabOffsetX += dragAmount.x
                        fabOffsetY += dragAmount.y
                    }
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.micro),
                contentDescription = "Micro",
                modifier = Modifier.size(AppTheme.dimen.iconLargeSize),
                tint = AppTheme.color.textButtonColor
            )
        }
    }

}


@Composable
fun WeatherCard(
    temperature: String,
    humidity: String,
    isRaining : Boolean,
    onTahActivity:()->Unit,
    onSwitch:()->Unit,
    onLogout:()->Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppTheme.dimen.heightLargeImage)
            .clickable{
                onTahActivity()
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.house_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
        Icon(
            painter = painterResource(R.drawable.log_out),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopStart).clickable{onLogout()}
                .padding(top = AppTheme.padding.paddingBar, start = AppTheme.padding.smallHorizontalPadding).size(AppTheme.dimen.iconLargeSize),
            tint = AppTheme.color.textButtonColor
        )
        Icon(
            painter = painterResource(R.drawable.home),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd).clickable{onSwitch()}
                .padding(top = AppTheme.padding.paddingBar, end = AppTheme.padding.largeHorizontalPadding).size(AppTheme.dimen.iconLargeSize),
            tint = AppTheme.color.textButtonColor
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    horizontal = AppTheme.padding.largeHorizontalPadding,
                    vertical = AppTheme.padding.largeVerticalPadding

                ),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacer.multiItemGap),
            verticalAlignment = Alignment.CenterVertically

        ) {
            // Temperature block
            Column {
                Text(
                    text = "${temperature}°C",
                    style = AppTheme.typography.infoLargeTitle,
                    color = AppTheme.color.textButtonColor
                )
                Text(
                    text = "Temperature",
                    style = AppTheme.typography.placeHolder,
                    color = AppTheme.color.policyColor
                )
            }

            // Humidity block
            Column {
                Text(
                    text = "${humidity}%",
                    style = AppTheme.typography.infoLargeTitle,
                    color = AppTheme.color.textButtonColor
                )
                Text(
                    text = "Humidity",
                    style = AppTheme.typography.placeHolder,
                    color = AppTheme.color.policyColor
                )
            }
            Icon(
                painter= painterResource(if(isRaining) R.drawable.wet else R.drawable.no_rain),
                contentDescription = null,
                modifier = Modifier.size(AppTheme.dimen.iconLargeSize),
                tint = AppTheme.color.textButtonColor
            )
        }
    }
}




@Composable
fun Section(name:String ,list: List<General>,viewmodel: MainViewmodel,onNextActivity:( Class<out Activity>)->Unit){
    Text(
        modifier= Modifier.padding(horizontal =AppTheme.padding.smallHorizontalPadding),
        text = name,
        style = AppTheme.typography.introSectionTitle,
    )
    Spacer(modifier = Modifier.height(AppTheme.spacer.heightDash))
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal =AppTheme.padding.largeHorizontalPadding)
    ) {
        list.TwoInRow {
                d1,d2->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Device(
                    checked = d1.checked,
                    modifier= Modifier.width(AppTheme.dimen.widthDevice).height(AppTheme.dimen.heightDevice),
                    d1.iconId,d1.name,d1.addition,
                    d1.selectedColor,d1.unselectedColor,
                    d1.background,d1.unSelectedBackground,
                    onSwitch = {
                        it->
                        d1.onSwitch(it)
                    },
                    onNextActivity = {
                        d1.activity?.let {
                            onNextActivity(it)
                        }
                    }
                )
                d2?.apply {
                    Device(
                        checked = d2.checked,
                        modifier= Modifier.width(AppTheme.dimen.widthDevice).height(AppTheme.dimen.heightDevice),
                        d2.iconId,d2.name,d2.addition,
                        d2.selectedColor,d2.unselectedColor,
                        d2.background,d2.unSelectedBackground,
                        onSwitch = {
                            d2.onSwitch(it)
                        },
                        onNextActivity = {
                            d2.activity?.let {
                                onNextActivity(it)
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(AppTheme.spacer.heightDash))
        }


    }




}
@Composable
fun List<General>.TwoInRow(content :@Composable (General, General?)->Unit){
    var size = this.size / 2
    if(this.size %2 !=0){
        size += 1
    }
    var i=0;
    while(i< size){
        if(2*i+1 >= this.size){
            content(this[2*i], null)

        }else{
            content(this[2*i], this[2*i+1])

        }
        i++
    }
}