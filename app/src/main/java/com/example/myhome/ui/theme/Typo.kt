package com.example.myhome.ui.theme

import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

data class AppTypo(
    val infoLargeTitle : TextStyle = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
    ),
    val infoAdditionTitle: TextStyle = TextStyle( fontSize = 16.sp,),
    val policyTitle :TextStyle = TextStyle(fontSize = 11.sp, textAlign = TextAlign.Center),
    val largeButtonTitle :TextStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
    val placeHolder :TextStyle = TextStyle(fontSize = 14.sp),
    val additionTitle :TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
    val deviceLargeTitle:TextStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold,fontFamily = FontFamily.SansSerif),
    val introSectionTitle :TextStyle = TextStyle(
        fontSize = 20.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold
    ),
    val deviceSmallTitle:TextStyle = TextStyle(fontSize = 14.sp, fontFamily = FontFamily.SansSerif)
)