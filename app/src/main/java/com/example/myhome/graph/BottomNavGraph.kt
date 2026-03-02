package com.example.myhome.graph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.myhome.domain.home.Room
import com.example.myhome.util.CustomNaType
import com.example.myhome.viewmodel.MainEvent
import com.example.myhome.viewmodel.MainViewmodel
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Composable
fun BottomNavGraph(navController: NavHostController,viewmodel: MainViewmodel,modifier:Modifier){
    NavHost(navController = navController,
        modifier = modifier,
        startDestination = HomeGraph){
        navigation(
            route = HomeGraph::class,
            startDestination = MainRoot::class
        ){
            composable<MainRoot>{
                viewmodel.switchScreen(MainEvent.HomeScreenEvent)
                MainScreen(
                    viewmodel =viewmodel,
                    modifier = Modifier
                ){
                    navController.navigate(RoomRoot(it))
                }
            }
            composable<RoomRoot>(
                typeMap = mapOf(
                    typeOf<Room>() to CustomNaType.RoomType
                )
            ){
                val arguments = it.toRoute<RoomRoot>()
                viewmodel.switchScreen(MainEvent.RoomDetailEvent(arguments.room.id?:"TDimibCauJt19O4hwBHh"))
                RoomDetailScreen(modifier = modifier,room = arguments.room,viewmodel = viewmodel)
            }
        }
        composable<NotificationRoot> {
        }
        composable<AccountRoot> {
            AccountScreen()
        }


    }

}


@Serializable
object HomeGraph

@Serializable
object MainRoot


@Serializable
object NotificationRoot

@Serializable
object AccountRoot

@Serializable
data class RoomRoot(val room: Room)
