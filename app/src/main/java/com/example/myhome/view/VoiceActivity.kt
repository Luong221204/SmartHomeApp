package com.example.myhome.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.myhome.R
import com.example.myhome.domain.response.Result
import com.example.myhome.viewmodel.VoiceViewmodel
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class VoiceActivity : BaseActivity() {
    val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
    val viewmodel : VoiceViewmodel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()
        enableEdgeToEdge()
        setContent {
            VoiceScreen(
                speechRecognizer,
                spokenText = viewmodel.text,
                viewmodel,
                onClose = { finish() },
            )

        }
    }

    override fun onStop() {
        super.onStop()
        speechRecognizer.stopListening()
    }


    private fun askNotificationPermission() {
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
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
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



fun startSpeechRecognition( speechRecognizer: SpeechRecognizer,onResult: (String) -> Unit,onTimeOut:()->Unit,onRealtime:(String)->Unit) {

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

    }

    speechRecognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {

        }

        override fun onError(error: Int) {
            when (error) {
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> onTimeOut()
                SpeechRecognizer.ERROR_NO_MATCH ->
                    Log.d("DUCLUONG", "SpeechRecognizer: Không nhận dạng được")
                else ->
                    Log.d("DUCLUONG", "SpeechRecognizer error: $error")
            }
        }

        override fun onResults(results: Bundle?) {
            val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            onResult(data?.get(0) ?: "")
        }

        override fun onPartialResults(partialResults: Bundle?) {

            val data = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            onRealtime(data?.get(0) ?: "")
        }
        override fun onEvent(eventType: Int, params: Bundle?) {

        }
    })

    speechRecognizer.startListening(intent)
}
@Composable
fun VoiceScreen(
    speechRecognizer: SpeechRecognizer,
    spokenText: String,
    viewmodel: VoiceViewmodel,
    onClose: () -> Unit,
) {
    var isListening by remember {
        mutableStateOf(false)
    }
    val isSending = viewmodel.response.collectAsState(Result.Nothing)
    LaunchedEffect(Unit) {
        snapshotFlow { isListening }.collect { listening ->
            if (listening) {
                startSpeechRecognition(
                    speechRecognizer,
                    onResult = { viewmodel.updateText(it) },
                    onRealtime = { viewmodel.updateText(it) },
                    onTimeOut = { isListening = false }
                )
            } else {
                speechRecognizer.stopListening()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000))
    ) {

        // Nút đóng
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top=32.dp, start = 4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.close),
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            VoiceMicPulse(isListening = isListening)

            Spacer(Modifier.height(28.dp))

            Text(
                text = spokenText.ifEmpty { "Đang lắng nghe…" },
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
        AnimatedMicButton(
            isSending.value,
            isListening = isListening,
            onMicClick = {
                isListening = !isListening
            },
            onSendRequest = {
                isListening= !isListening
                viewmodel.sendMessage(spokenText)

            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
        )
    }
}
@Composable
fun AnimatedMicButton(
    isSending: Result,
    isListening: Boolean,
    onMicClick: () -> Unit,
    onSendRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Màu nền
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSending is Result.Loading -> Color.Red.copy(alpha = 0.5f) // sending
            isListening -> Color.Red // listening
            else -> Color.Red // idle
        },
        label = ""
    )

    // Shape
    val shape by animateDpAsState(
        targetValue = if (isListening || isSending is Result.Loading) 20.dp else 50.dp,
        label = ""
    )

    // Size
    val sizeModifier = if (isListening || isSending is Result.Loading) {
        modifier.height(50.dp).width(120.dp)
    } else modifier.size(78.dp)

    FloatingActionButton(
        onClick = {
            if (isSending !is Result.Loading) {
                if (isListening) onSendRequest() else onMicClick()
            }
        },
        containerColor = backgroundColor,
        shape = RoundedCornerShape(shape),
        modifier = sizeModifier
    ) {
        when {
            isSending is Result.Loading -> {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(26.dp)
                )
            }
            isListening -> {
                Text(
                    text = "Gửi yêu cầu",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            else -> {
                Icon(
                    painter = painterResource(R.drawable.micro),
                    contentDescription = "Mic",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
fun VoiceMicPulse(isListening: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier
            .size(150.dp)
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Color(0x55FFFFFF))
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.micro),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}


