package com.xunse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.xunse.app.data.HistoryRepository
import com.xunse.app.data.SettingsRepository
import com.xunse.app.ui.XunseApp
import com.xunse.app.ui.theme.XunseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val settings = SettingsRepository(applicationContext)
        val history = HistoryRepository(applicationContext)
        setContent {
            XunseTheme {
                XunseApp(settings, history)
            }
        }
    }
}
