package com.mostafa.majiddelbandam

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.mostafa.majiddelbandam.audio.LocalGameAudio
import com.mostafa.majiddelbandam.di.LocalAppContainer
import com.mostafa.majiddelbandam.ui.navigation.MajidNavGraph
import com.mostafa.majiddelbandam.ui.theme.MajidDelbandamTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val locale = Locale("fa")
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as MajidApp
        setContent {
            CompositionLocalProvider(
                LocalAppContainer provides app.container,
                LocalGameAudio provides app.container.audio
            ) {
                MajidDelbandamTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        MajidNavGraph()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (application as MajidApp).container.audio.enterForeground()
    }

    override fun onPause() {
        (application as MajidApp).container.audio.enterBackground()
        super.onPause()
    }
}
