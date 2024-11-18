package com.example.webrtcsampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.webrtcsampleapp.navigation.WebRTCAppNavigation
import com.example.webrtcsampleapp.ui.theme.WebRTCSampleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebRTCSampleAppTheme {
                WebRTCAppNavigation()
            }
        }
    }
}

