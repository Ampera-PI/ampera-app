package com.ampera.dados

actual fun urlDaApi(): String {
    val host = kotlinx.browser.window.location.hostname
    val protocolo = kotlinx.browser.window.location.protocol
    return "$protocolo//$host:8080"
}

actual fun lerToken(): String? = kotlinx.browser.localStorage.getItem("ampera-token")

actual fun gravarToken(token: String?) {
    if (token.isNullOrBlank()) {
        kotlinx.browser.localStorage.removeItem("ampera-token")
    } else {
        kotlinx.browser.localStorage.setItem("ampera-token", token)
    }
}
