package org.example.skatecoinrush

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.skatecoinrush.di.initializeKoin

fun main() = application {
    initializeKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Skate Coin Rush",
    ) {
        App()
    }
}