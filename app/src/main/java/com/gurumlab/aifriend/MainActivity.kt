package com.gurumlab.aifriend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.gurumlab.aifriend.ui.theme.AIFriendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIFriendTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//
//                }
            }
        }
    }
}

