package com.example.myhome.view

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myhome.R
import com.example.myhome.compose.device.AutomationCreatorScreen
import com.example.myhome.compose.device.DetailSettingSection
import com.example.myhome.compose.device.ProfessionalAutomationScreen
import com.example.myhome.compose.house.RoomCard
import com.example.myhome.compose.skeleton.ActivityLogItemSkeleton
import com.example.myhome.compose.skeleton.AutomationSceneListSkeleton
import com.example.myhome.compose.skeleton.FullCardSkeleton
import com.example.myhome.compose.skeleton.LogListSkeleton
import com.example.myhome.compose.skeleton.ShimmerDeviceListItem
import com.example.myhome.compose.templates.DoubleInRow
import com.example.myhome.domain.home.House
import com.example.myhome.domain.home.Room
import com.example.myhome.graph.HouseWeatherCard
import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.ui.theme.Pink40
import com.example.myhome.ui.theme.Purple80
import com.example.myhome.viewmodel.DeviceViewmodel
import com.example.myhome.viewmodel.MainViewmodel
import com.example.myhome.viewmodel.Resource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DeviceActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewmodel : DeviceViewmodel by viewModels()
        setContent {
            val s = viewmodel.x.collectAsState()
            Log.d("DUCLUONG", "onCreate: ${s.value.list}")
            AppTheme {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProfessionalAutomationScreen()
                   //ExpandableSensorCard("Độ ẩm", R.drawable.humidity, "%", Color(0xFF2196F3))
                }
            }
        }
    }


}


@Composable
fun ExpandableSensorCard(
    name: String,
    icon: Int,
    unit: String,
    color: Color
) {
    // State quản lý việc đóng/mở
    var isExpanded by remember { mutableStateOf(false) }

    // State cho các thông số bên trong
    var threshold by remember { mutableStateOf(30f) }
    var operator by remember { mutableStateOf(">") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize( // Tự động làm mượt khi kích thước Card thay đổi
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) color.copy(alpha = 0.1f) else Color(0xFFF5F5F5)
        ),
        border = if (isExpanded) BorderStroke(2.dp, color) else null
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // --- PHẦN HEADER (Luôn hiển thị) ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Icon mũi tên xoay khi đóng/mở
                Icon(
                    painter = painterResource(R.drawable.downward),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).rotate(if (isExpanded) 180f else 0f),
                    tint = Purple80
                )
            }

            // --- PHẦN CHI TIẾT (Trượt xuống khi isExpanded = true) ---
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    Divider(color = color.copy(alpha = 0.2f))
                    Spacer(Modifier.height(16.dp))

                    // Thiết lập ngưỡng
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Khi giá trị $operator", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${threshold.toInt()}$unit",
                            style = MaterialTheme.typography.titleLarge,
                            color = color,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Slider(
                        value = threshold,
                        onValueChange = { threshold = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
                    )

                    // Chọn toán tử
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(">", "<", "=").forEach { op ->
                            val selected = operator == op
                            FilterChip(
                                selected = selected,
                                onClick = { operator = op },
                                label = { Text(op, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = color,
                                    selectedLabelColor = Pink40
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

