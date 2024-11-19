package com.example.webrtcsampleapp.navigation

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.webrtcsampleapp.screens.HomeScreen
import com.example.webrtcsampleapp.screens.PermissionScreen
import com.example.webrtcsampleapp.screens.VideoCallScreen
import timber.log.Timber

@Composable
fun WebRTCAppNavigation() {

    CompositionLocalProvider(value = LocalNavController provides rememberNavController()) {
        SetupNavigation()
    }
}
@Composable
fun SetupNavigation() {
    var navController = LocalNavController.current;
    var context = LocalContext.current;
    var isCameraPermissionGranted = ContextCompat.checkSelfPermission(context,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    NavHost(navController = navController, startDestination = if(isCameraPermissionGranted) "Home" else "Permission"){
        composable(route = "Home") {
            HomeScreen(navigateToCallScreen = { roomId ->
                    navController.navigate("VideoCall/${roomId}")
            })

        }
        composable(route = "Permission") {
            PermissionScreen(navigateToHomeScreen = {
                navController.navigate("Home"){
                    popUpTo(0){inclusive=true}
                    launchSingleTop = true
                }
            })
        }
        composable(route = "VideoCall/{roomIdParam}") {backStartEntry ->
            val roomId = backStartEntry.arguments?.getString("roomIdParam","");
            if(roomId.isNullOrEmpty()){
                Toast.makeText(context,"Room ID Not Available !!!",Toast.LENGTH_LONG).show()
                Timber.e("Room ID Not Available !!!")

                return@composable
            }
            VideoCallScreen(roomId, onNavigateBack = {
                /*navController.navigate("Home"){
                    popUpTo(0){inclusive=true}
                    launchSingleTop = true
                }*/
            })
        }
    }
}

val LocalNavController = compositionLocalOf<NavHostController> { error("No LocalNavController Provided !") }