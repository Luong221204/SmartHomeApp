package com.example.myhome

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.myhome.compose.Device
import com.example.myhome.domain.General
import com.example.myhome.ui.theme.MyHomeTheme
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        askNotificationPermission()
        val viewmodel: MainViewmodel by viewModels()
        val humid = viewmodel.humid
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        setContent {
            MyHomeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        viewmodel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart() }
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
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
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
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
fun MainScreen(viewmodel: MainViewmodel,modifier: Modifier = Modifier){
    val temp = viewmodel.temp
    val humid = viewmodel.humid
    val gs = viewmodel.gs


    Column(
        modifier= modifier.verticalScroll(rememberScrollState()),
    ) {
        TemperatureAndHumidity(temp,humid,modifier= Modifier
            .height(50.dp)
            .width(250.dp))
        Click(viewmodel)
        Spacer(modifier = Modifier.height(20.dp))
        Section("Thiết bị", viewmodel.deviceList,viewmodel)
        Section("Cảm biến", viewmodel.sensorList,viewmodel)
    }
}

@Composable
fun TemperatureAndHumidity(temp: String, humid:String,modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.temperature),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "Nhiệt độ : $temp",
            style = TextStyle(fontSize = 14.sp, color = Color.Red)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Image(
            painter = painterResource(R.drawable.humidity),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "Độ ẩm : $humid",
            style = TextStyle(fontSize = 14.sp, color = Color.Blue)
        )
    }
}



@Composable
fun Click(viewmodel: MainViewmodel){
    Button(
        onClick = {
            viewmodel.updateFan(false)
        },
        modifier = Modifier.height(50.dp).width(100.dp),
        colors = ButtonDefaults.buttonColors(contentColor = Color.Blue)
    ) {
        Text(text = "click")
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