package com.example.medicitas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import com.example.medicitas.src.Features.Navigation.AppNavigation
import com.example.medicitas.ui.theme.MediCitasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediCitasTheme {
                AppNavigation()
            }
        }
    }
}

