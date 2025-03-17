package org.example.skatecoinrush

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Skate Coin Rush",
    ) {
        App()
    }
}