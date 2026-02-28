package com.example.myhome.compose.templates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.example.myhome.R

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReusableComposeNode
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar() {
    TopAppBar(
        // 1. Container Color
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF23263A), // Màu tím như trong hình
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),

        // 2. Navigation Icon
        navigationIcon = {
            IconButton(onClick = { /* Xử lý mở Drawer */ }) {
                Icon(painter = painterResource(R.drawable.back) , contentDescription = "Menu",
                    modifier = Modifier.size(18.dp)
                    )
            }
        },

        // 3, 4, 5. Title & Subtitle + Logo
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 3. Leading Logo
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery), // Thay bằng icon hexagon của bạn
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp).padding(end = 12.dp),
                    tint = Color.White
                )

                Column {
                    // 4. Title
                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp
                    )
                    // 5. Subtitle
                    Text(
                        text = "Subtitle",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        },
        actions = {

        }


    )
}