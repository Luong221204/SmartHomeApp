# SmartHomeApp

## Overview
Ứng dụng điều khiển thiết bị Smart Home (đèn, quạt...) thông qua server NestJs và Firebase.
Server chạy local

## Features
- Bật/tắt thiết bị
- Giao diện realtime
- Quản lý trạng thái thiết bị

## Tech Stack
- Android (Kotlin/Java)
- Firebase Storage, Firebase AI,YChart
- MVVM Architecture
- Push Notification + Firebase cloud messaging
- Socket.IO
  

## Architecture
- UI:Jetpack Compose
- ViewModel
- flow :StateFlow,SharedFlow
- side effect :LaunchedEffect , rememberCoroutineScope , snapShotFlow ,...
- state hoisting
- compostion Local + Apptheme quản ly theme
