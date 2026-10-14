package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.VisionHomeScreen
import com.example.ui.VisionViewModel
import com.example.ui.theme.VisionSceneTheme

class MainActivity : ComponentActivity() {

    private val viewModel: VisionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VisionSceneTheme {
                VisionHomeScreen(viewModel = viewModel)
            }
        }
    }
}
