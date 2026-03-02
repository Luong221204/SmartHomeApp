package com.example.myhome.graph
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myhome.R
import com.example.myhome.network.api.Staff
import com.example.myhome.util.Constants

// Định nghĩa mã màu chính xác từ ảnh
val DarkBackground = Color(0xFF1B202D) // Màu nền của Scaffold
val CardBackground = Color(0xFF242A3D) // Màu nền của Card
val TextPrimary = Color.White
val TextSecondary = Color(0xFF8B93A5) // Màu xám của chữ "Off"
val SwitchThumbColor = Color(0xFF2E8CFF) // Màu xanh của nút Switch

@Composable
fun FanControlCard(
    staff: Staff
) {
    // State để quản lý trạng thái Bật/Tắt của Switch
    var isChecked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .size(width = 170.dp, height = 180.dp), // Kích thước xấp xỉ của Card
        shape = RoundedCornerShape(12.dp), // Góc bo tròn lớn
        colors = CardDefaults.cardColors(
            containerColor = CardBackground // Màu nền tối của Card
        )
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ){
            val guideline = createGuidelineFromEnd(0.4f)

            val (icon,name,status,switch) = createRefs()
            Icon(
                painter = painterResource(Constants.deviceList[staff.type]?:R.drawable.home), // Dùng tạm icon hệ thống
                contentDescription = "Fan Icon",
                tint = Color(0xFFA5D6A7), // Màu xanh lá mờ của icon quạt
                modifier = Modifier.size(32.dp).
                constrainAs(icon){
                    top.linkTo(parent.top,12.dp)
                    start.linkTo(parent.start,12.dp)
                }
            )
            Text(
                text = staff.name?:"",
                color = TextPrimary,
                fontSize = 15.sp,
                modifier = Modifier.constrainAs(name){
                    top.linkTo(icon.bottom)
                    start.linkTo(icon.start)
                    end.linkTo(parent.end,0.dp)
                    horizontalBias = 0f
                    bottom.linkTo(status.top)

                }
            )
            Text(
                text = if(staff.status == true) "On" else "Off",
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.constrainAs(status){
                    top.linkTo(switch.top)
                    bottom.linkTo(switch.bottom)
                    start.linkTo(icon.start)
                }
            )
            Switch(
                checked = staff.status == true,
                onCheckedChange = { isChecked = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SwitchThumbColor, // Màu xanh khi bật
                    checkedTrackColor = SwitchThumbColor.copy(alpha = 0.5f),
                    uncheckedThumbColor = TextSecondary, // Màu xám khi tắt
                    uncheckedTrackColor = DarkBackground // Nền Switch khi tắt
                ),
                modifier = Modifier.constrainAs(switch){
                    bottom.linkTo(parent.bottom,12.dp)
                    end.linkTo(parent.end,12.dp)
                }
            )


        }
    }
}

