package org.example.skatecoinrush.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import skatecoinrush.composeapp.generated.resources.Res
import skatecoinrush.composeapp.generated.resources.chewy_regular

@Composable
fun GameFontFamily() = FontFamily(Font(Res.font.chewy_regular))