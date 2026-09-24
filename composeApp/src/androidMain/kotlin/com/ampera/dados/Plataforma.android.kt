package com.ampera.dados

import android.content.Context
import com.ampera.ContextoAndroid

actual fun urlDaApi(): String = "http://10.0.2.2:8080"

actual fun lerToken(): String? =
    prefs().getString("token", null)

actual fun gravarToken(token: String?) {
    prefs().edit().putString("token", token).apply()
}

private fun prefs() = ContextoAndroid.contexto.getSharedPreferences("ampera", Context.MODE_PRIVATE)
