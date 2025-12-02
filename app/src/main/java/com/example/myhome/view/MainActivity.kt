package com.example.myhome.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.core.view.WindowCompat
import com.example.myhome.R
import com.example.myhome.compose.Device
import com.example.myhome.domain.General
import com.example.myhome.domain.Password
import com.example.myhome.ui.theme.BackgroundColor
import com.example.myhome.ui.theme.MyHomeTheme
import com.example.myhome.viewmodel.MainViewmodel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.math.roundToInt


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        askNotificationPermission()
        val viewmodel: MainViewmodel by viewModels()
        val humid = viewmodel.humid

        setContent {
            MyHomeTheme {
                Scaffold(modifier = Modifier.fillMaxSize().background(color = Color.Black)) { innerPadding ->
                    MainScreen(
                        viewmodel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 44.dp),
                        {
                            val intent = Intent(this, PasswordActivity::class.java)
                            startActivity(intent)
                        }
                    ){
                        val intent = Intent(this, VoiceActivity::class.java)
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
fun MainScreen(viewmodel: MainViewmodel, modifier: Modifier = Modifier,onSwitch: () -> Unit,onVoiceScreen:()->Unit) {
    val temp = viewmodel.temp
    val humid = viewmodel.humid
    val gs = viewmodel.gs

// State lưu vị trí FAB
    var fabOffsetX by remember { mutableFloatStateOf(0f) }
    var fabOffsetY by remember { mutableFloatStateOf(0f) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            WeatherCard(temp, humid,onSwitch)
            Spacer(modifier = Modifier.height(20.dp))
            Section("Thiết bị", viewmodel.deviceList, viewmodel)
            Section("Cảm biến", viewmodel.sensorList, viewmodel)
        }

        // Floating button micro
        FloatingActionButton(
            onClick = { onVoiceScreen()},
            shape = CircleShape,
            containerColor = Color.Red,
            modifier = Modifier
                .offset { IntOffset(fabOffsetX.roundToInt(), fabOffsetY.roundToInt()) }
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(80.dp)
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
                modifier = Modifier.size(28.dp),
                tint = Color.White
            )
        }
    }

}


@Composable
fun WeatherCard(
    temperature: String,
    humidity: String,
    onSwitch:()->Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(232.dp)
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.house_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay gradient (tối nhẹ ảnh để chữ sáng hơn)
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
            painter = painterResource(R.drawable.key),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd).clickable{onSwitch()}
                .padding(top = 40.dp, end = 24.dp).size(24.dp),
            tint = Color.White
        )
        // Content
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(50.dp)
        ) {
            // Temperature block
            Column {
                Text(
                    text = "${temperature}°C",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Temperature",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // Humidity block
            Column {
                Text(
                    text = "${humidity}%",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Humidity",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}




@Composable
fun Section(name:String ,list: List<General>,viewmodel: MainViewmodel){
    Text(
        modifier= Modifier.padding(horizontal = 10.dp),
        text = name,
        style = TextStyle(
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold
        )
    )
    Spacer(modifier = Modifier.height(20.dp))
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp)
    ) {
        list.TwoInRow {
                d1,d2->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Device(
                    checked = d1.checked,
                    modifier= Modifier.width(150.dp).height(120.dp),
                    d1.iconId,d1.name,d1.addition,
                    d1.selectedColor,d1.unselectedColor,
                    d1.background,d1.unSelectedBackground,
                    onSwitch = {
                        it->
                        d1.onSwitch(it)
                    }
                )
                d2?.apply {
                    Device(
                        checked = d2.checked,
                        modifier= Modifier.width(150.dp).height(120.dp),
                        d2.iconId,d2.name,d2.addition,
                        d2.selectedColor,d2.unselectedColor,
                        d2.background,d2.unSelectedBackground,
                        onSwitch = {
                            d2.onSwitch(it)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
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