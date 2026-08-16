package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.MaqalTheme
import com.example.ui.viewmodel.FinanceViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FinanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaqalTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}
