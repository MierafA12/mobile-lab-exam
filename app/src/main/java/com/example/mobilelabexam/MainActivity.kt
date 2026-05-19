package com.example.mobilelabexam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mobilelabexam.ui.components.AppNavigation
import com.example.mobilelabexam.ui.theme.MobilelabexamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobilelabexamTheme {
                AppNavigation()
            }
        }
    }
}