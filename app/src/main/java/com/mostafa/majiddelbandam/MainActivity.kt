package com.mostafa.majiddelbandam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mostafa.majiddelbandam.ui.theme.MajidDelbandamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MajidDelbandamTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    EmptyGameScreen(modifier = Modifier.safeDrawingPadding())
                }
            }
        }
    }
}

@Composable
fun EmptyGameScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Preview(showBackground = true, locale = "fa")
@Composable
private fun EmptyGameScreenPreview() {
    MajidDelbandamTheme {
        EmptyGameScreen()
    }
}
