package com.example.myhome.compose.device

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.ui.theme.Purple80

@Composable
fun RowValue(
    list: Map<Int, Int>?,
    onSelect: (Int) -> Unit,
    selected: Int,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ){
        Text("Chọn mức", style = AppTheme.typography.deviceLargeTitle)
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            list?.forEach {
                val isSelected = it.key == selected
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) Purple80 else Color.Transparent,
                        contentColor = if (isSelected) Color.White else Purple80
                    ),
                    contentPadding = PaddingValues(8.dp) ,
                    onClick = {
                        onSelect(it.key)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Purple80)
                ) {
                    Text("${it.key*100/ list.size} %",style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp))
                }
            }
        }
    }
}