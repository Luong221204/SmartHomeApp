package com.example.myhome.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation

@Composable
fun BottomNavGraph(navController: NavHostController){
    NavHost(navController = navController,
        startDestination = "Home_Graph"){
        navigation(
            route = "Home_Graph",
            startDestination = "Home"
        ){
            composable(route = "Home"){
                //MainScreen()
            }
            composable(route = "Creation"){
                CreationScreen()
            }
        }
        navigation(
            route = "Creation_Graph",
            startDestination = "Chart"
        ){
            composable(route = "Chart"){
                CreationScreen()
            }
            composable(route = "Home"){
               // MainScreen()
            }
        }
        navigation(
            route = "Account_Graph",
            startDestination = "Account"
        ){
            composable(route = "Account"){
                AccountScreen()
            }
        }


    }

}