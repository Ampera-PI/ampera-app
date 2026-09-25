package com.ampera

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.ampera.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Ampéra",
        state = rememberWindowState(width = 1200.dp, height = 800.dp),
    ) {
        App()
    }
}
