package com.example.myhome.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myhome.R
import com.example.myhome.graph.BottomBar

import com.example.myhome.graph.BottomNavGraph

import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.ui.theme.MyHomeTheme
import com.example.myhome.viewmodel.MainViewmodel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable


@AndroidEntryPoint
class MainActivity : BaseActivity() {
    val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        askNotificationPermission()
        val viewmodel: MainViewmodel by viewModels()
        setContent {
            val coroutine = rememberCoroutineScope()
            val navController = rememberNavController()
            val controller = rememberNavController()
            AppTheme {
                NavHost(
                    modifier = Modifier.fillMaxSize(),
                    navController = controller,
                    startDestination = MainGraph::class
                ){
                    composable<MainGraph> {
                        Scaffold(modifier = Modifier.fillMaxSize().background(color = Color.Black),
                            bottomBar = {
                                BottomBar(navController)
                            }
                        ) { innerPadding ->
                            BottomNavGraph(navController,viewmodel, modifier = Modifier.padding(innerPadding).fillMaxSize()){
                                controller.navigate(VoiceGraph)
                            }
                        }
                    }
                    composable<VoiceGraph> {
                        Scaffold(
                            modifier = Modifier.fillMaxSize()
                        ) {
                                it->
                            VoiceScreen(
                                speechRecognizer= speechRecognizer,
                                modifier = Modifier.padding(it),
                                viewmodel = viewmodel
                            )
                        }
                    }
                }

            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
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
    private fun askAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
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
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {

            } else {
                requestPermissionLauncher2.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
    private val requestPermissionLauncher2 = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this,getString(R.string.success_statement),Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.warning_statement),Toast.LENGTH_SHORT).show()

        }
    }
}
@Serializable
object MainGraph

@Serializable
object VoiceGraph
