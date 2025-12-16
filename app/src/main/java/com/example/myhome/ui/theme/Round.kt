package com.example.myhome.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class RoundedCorner(
    val buttonCorner: CornerBasedShape = RoundedCornerShape(15.dp),
    val dialogCorner: CornerBasedShape = RoundedCornerShape(24.dp),
    val switchCorner: CornerBasedShape = RoundedCornerShape(50.dp),

)