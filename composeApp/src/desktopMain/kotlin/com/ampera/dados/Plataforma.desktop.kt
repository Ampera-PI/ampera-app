package com.ampera.dados

import java.io.File

actual fun urlDaApi(): String = "http://localhost:8080"

actual fun lerToken(): String? = arquivo().takeIf { it.exists() }?.readText()?.ifBlank { null }

actual fun gravarToken(token: String?) {
    val arquivo = arquivo()
    if (token.isNullOrBlank()) arquivo.delete() else arquivo.writeText(token)
}

private fun arquivo() = File(System.getProperty("user.home"), ".ampera-token")
