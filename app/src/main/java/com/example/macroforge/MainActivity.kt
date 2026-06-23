package com.example.macroforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.macroforge.core.navigation.MacroForgeNavHost
import com.example.macroforge.core.navigation.theme.MacroForgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MacroForgeTheme {
                MacroForgeNavHost()
            }
        }
    }
}
