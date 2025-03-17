package org.example.skatecoinrush.util

enum class Platform{
    Android,
    iOS,
    Desktop,
    Web
}

expect fun getPlatform(): Platform