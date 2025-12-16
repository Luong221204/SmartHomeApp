package com.example.myhome.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val Brown  = Color(0xFF004d00)
val EmergencyColor = Color( 0xFFcc0000)
val DeviceColor = Color.Black
val BackgroundColor = Color(0xFF19191b)


data class AppColor(
    val purple80 :Color = Color(0xFFD0BCFF),
    val purpleGrey80:Color = Color(0xFFCCC2DC),
    val pink80:Color = Color(0xFFEFB8C8),
    val purple40 :Color= Color(0xFF6650a4),
    val purpleGrey40:Color = Color(0xFF625b71),
    val pink40:Color = Color(0xFF7D5260),
    val brown :Color = Color(0xFF004d00),
    val emergencyColor :Color= Color( 0xFFcc0000),
    val deviceColor :Color= Color.Black,
    val backgroundColor :Color= Color(0xFF19191b),
    val largeButton :Color =  Color(0xFFDAA520),
    val infoLargeColor :Color = Color.White,
    val infoAdditionColor :Color = Color.White.copy(0.8f),
    val policyColor:Color = Color.Gray,
    val textButtonColor:Color = Color.White,
    val circularButton:Color = Color.White,
    val unEnableButton:Color = Color.Gray,
    val enableButton:Color = Color(0xFFDAA520),
    val additionColor:Color = Color(0xFFDAA520),
    val backgroundAppColor:Color = Color(0xFFF2F2F2),
    val floatingButtonColor:Color = Color.Red,
    val wave :Color =Color(0x55FFFFFF),
    val transparent :Color = Color(0xAA000000),
    val switchOn :Color = Color(0xFF34C759),
    val switchOff:Color =Color(0xFFBFBFBF)



)