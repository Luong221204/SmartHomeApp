package com.example.myhome.compose.device

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhome.R
import com.example.myhome.ui.theme.Pink40
import com.example.myhome.ui.theme.Purple80

@Composable
fun ExpandableSensorCard(
    name: String,
    icon: Int,
    unit: String,
    min: Float,
    max: Float,
    operator: String,
    threshold:Float,
    color: Color,
    valueForDevice:Float,
    presets:Map<Int, Int>,
    onSetupValueChange:(Float)->Unit = {},
    actionStatus:Boolean = false,
    onActionChange:(Boolean)->Unit = {},
    onValueChanged:(Float)->Unit,
    onOperatorChange:(String)->Unit,
    onSetup:()-> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
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

                Icon(
                    painter = painterResource(R.drawable.downward),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).rotate(if (isExpanded) 180f else 0f),
                    tint = Purple80
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Divider(color = color.copy(alpha = 0.2f))
                    Spacer(Modifier.height(16.dp))
                    SetupThreshold(threshold,unit,operator,color,min,max,onValueChanged,onOperatorChange)// thiết lập ngưỡng

                    Divider(color = Color.LightGray.copy(alpha = 0.3f))

                    Spacer(Modifier.height(16.dp))

                    ActionSwitch(actionStatus,color,onActionChange)// thay đổi trạng thái bat tat thiet bi
                    if(actionStatus) Spacer(Modifier.height(16.dp))
                    AnimatedVisibility(
                        visible = actionStatus,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        SetupValue(presets,current = valueForDevice,color,onSetupValueChange) // thiết lập mức cho thiết bị
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Thiết lập",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.clickable{
                            onSetup()
                        },
                    )
                }
            }
        }
    }
}
@Composable
fun ActionSwitch(
    actionStatus:Boolean,
    color: Color ,
    onActionChange:(Boolean)->Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Trạng thái thiết bị:", style = MaterialTheme.typography.titleSmall)

        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.2f))
                .padding(4.dp)
        ) {
            val statusOptions = listOf(true to "BẬT", false to "TẮT")
            statusOptions.forEach { (status, label) ->
                val isSelected = actionStatus == status
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) color else Color.Transparent)
                        .clickable {
                            onActionChange(!actionStatus)
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else Color.Gray,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SetupValue(
    list:Map<Int,Int>,
    current:Float = 0f,
    color: Color,
    onSetupValueChange:(Float)->Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        list.forEach { preset ->
            val isSelected = current == preset.key.toFloat()
            OutlinedButton(
                onClick = {
                    onSetupValueChange(preset.key.toFloat())
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) color else Color.Transparent,
                    contentColor = if (isSelected) Color.White else color
                ),
                contentPadding = PaddingValues(8.dp),
                border = BorderStroke(1.dp, color)
            ) {
                Text(text = "${(preset.key*100/list.size)}%", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

@Composable
fun SetupThreshold(
    threshold:Float,
    unit:String,
    operator:String,
    color:Color,
    min:Float,
    max:Float,
    onValueChanged:(Float)->Unit,
    onOperatorChange:(String)->Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Khi giá trị $operator", style = MaterialTheme.typography.titleSmall)
        Text(
            "${threshold.toInt()}$unit",
            style = MaterialTheme.typography.titleLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
    Slider(
        value = threshold,
        onValueChange = { onValueChanged(it) },
        valueRange = min..max,
        colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(">", "<", "=").forEach { op ->
            val selected = operator == op
            FilterChip(
                selected = selected,
                onClick = { onOperatorChange(op) },
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