package com.example.myhome.compose.skeleton

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

/**
 * Một Component hiển thị hiệu ứng sóng âm gồm các thanh nhảy múa.
 *
 * @param modifier Modifier tùy chỉnh cho bố cục bên ngoài.
 * @param color Màu của các thanh sóng.
 * @param barCount Số lượng thanh sóng hiển thị.
 * @param barWidth Độ rộng của mỗi thanh sóng.
 * @param maxBarHeight Chiều cao tối đa mà một thanh có thể đạt được.
 * @param isListening Nếu false, sóng sẽ phẳng (trạng thái tĩnh).
 */
@Composable
fun VoiceWaveformAnimation(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    barCount: Int = 12, // Số lượng thanh
    barWidth: Dp = 8.dp, // Độ rộng mỗi thanh
    maxBarHeight: Dp = 80.dp, // Chiều cao tối đa khi "nhảy"
    minBarHeight: Dp = 10.dp, // Chiều cao tối thiểu
    isListening: Boolean = true
) {
    // 1. Quản lý trạng thái chuyển đổi vô hạn (Infinite Transition)
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformTransition")

    // 2. Tạo danh sách các giá trị animatable cho chiều cao từng thanh
    val barHeightFractions = List(barCount) { index ->
        if (isListening) {
            // Nếu đang nghe, tạo animation riêng cho mỗi thanh
            infiniteTransition.animateFloat(
                initialValue = 0.2f, // Tỉ lệ chiều cao tối thiểu (20% max)
                targetValue = 1f,   // Tỉ lệ chiều cao tối đa
                animationSpec = infiniteRepeatable(
                    // Tạo sự ngẫu nhiên về thời gian để các thanh không nhảy giống hệt nhau
                    animation = tween(
                        durationMillis = Random.nextInt(400, 900),
                        delayMillis = index * 30, // Delay nhẹ giữa các thanh tạo hiệu ứng lan truyền
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse // Nhảy lên rồi thụt xuống
                ),
                label = "BarHeight_$index"
            )
        } else {
            // Nếu không nghe, giữ cố định ở chiều cao tối thiểu
            remember { mutableFloatStateOf(0.1f) } // 10% chiều cao
        }
    }

    // 3. Vẽ trên Canvas
    Canvas(
        modifier = modifier
            .width((barCount * (barWidth.value + 4.dp.value)).dp) // Tổng chiều rộng ước tính
            .height(maxBarHeight)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val maxH = maxBarHeight.toPx()
        val minH = minBarHeight.toPx()
        val widthBarPx = barWidth.toPx()

        // Tính toán khoảng cách giữa các thanh
        val totalBarWidth = barCount * widthBarPx
        val spaceBetween = if (barCount > 1) (canvasWidth - totalBarWidth) / (barCount - 1) else 0f

        for (i in 0 until barCount) {
            // Tính toán chiều cao thực tế của thanh này tại thời điểm hiện tại
            val peakHeight = maxH * barHeightFractions[i].value
            val currentHeight = maxOf(minH, peakHeight)

            val xOffset = i * (widthBarPx + spaceBetween)
            // Căn giữa thanh theo chiều dọc
            val yOffset = (canvasHeight - currentHeight) / 2f

            // Vẽ hình chữ nhật bo góc
            drawRoundRect(
                color = color,
                topLeft = Offset(xOffset, yOffset),
                size = Size(widthBarPx, currentHeight),
                cornerRadius = CornerRadius(widthBarPx / 2, widthBarPx / 2) // Bo tròn hoàn toàn đầu thanh
            )
        }
    }
}

// --- Preview để xem kết quả ---

@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun PreviewVoiceWaveform() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Đang lắng nghe...", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(30.dp))

            // Hiệu ứng sóng
            VoiceWaveformAnimation(
                color = Color(0xFF3F51B5), // Màu xanh Indigo
                barCount = 15,
                barWidth = 6.dp,
                maxBarHeight = 100.dp,
                isListening = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVoiceWaveformIdle() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Tạm dừng", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(30.dp))

            // Hiệu ứng sóng khi tắt
            VoiceWaveformAnimation(
                color = Color.Gray,
                barCount = 15,
                barWidth = 6.dp,
                maxBarHeight = 100.dp,
                isListening = false
            )
        }
    }
}