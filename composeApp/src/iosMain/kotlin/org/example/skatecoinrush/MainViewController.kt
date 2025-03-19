package org.example.skatecoinrush

import androidx.compose.ui.window.ComposeUIViewController
import org.example.skatecoinrush.di.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) { App() }