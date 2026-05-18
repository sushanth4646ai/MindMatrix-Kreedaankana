package com.kreedaankana.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kreedaankana.ui.navigation.AppNavigation
import com.kreedaankana.ui.theme.KreedaAnkanaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KreedaAnkanaTheme {
                AppNavigation()
            }
        }
    }
}
