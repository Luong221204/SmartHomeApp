package com.example.myhome.graph

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.myhome.R
import com.example.myhome.domain.home.Room
import com.example.myhome.ui.theme.AppTheme
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
                LaunchedEffect(Unit){
                    viewmodel.switchScreen(MainEvent.RoomDetailEvent(arguments.room.id?:"TDimibCauJt19O4hwBHh"))
                }
                RoomDetailScreen(navController = navController,modifier = modifier,room = arguments.room,viewmodel = viewmodel)
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
@Composable
fun BottomBar(navController:NavHostController){
    var selectedItem by remember { mutableStateOf("Home") }
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    NavigationBar(
        modifier = Modifier.height(100.dp)
    ) {
        EnumIcon.entries.forEach {it->
            NavigationBarItem(
                selected =currentDestination?.hierarchy?.any { v->
                    v.hasRoute(it.c::class)
                }==true ,
                onClick = {
                    selectedItem = it.name
                    navController.navigate(it.c){
                        popUpTo(HomeGraph::class){
                            saveState=true
                        }
                        launchSingleTop=true
                        restoreState = true
                    }
                },
                alwaysShowLabel = it.name == selectedItem,
                modifier = Modifier.size(60.dp),
                label = {
                    Text(it.name, style = AppTheme.typography.policyTitle)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = it.icon),
                        contentDescription = it.name,
                        modifier = Modifier.size(AppTheme.dimen.thumbSize)
                    )
                }
            )
        }
    }
}
enum class EnumIcon(
    val icon:Int,
    val selectedColor: Color,
    val unSelectedColor: Color,
    val c : Any
){
    Home(R.drawable.home, Color.Red,Color.Gray, HomeGraph),
    Notification(R.drawable.log_out,Color.Red,Color.Gray, NotificationRoot),
    Account(R.drawable.ic_user,Color.Red,Color.Gray, AccountRoot)
}