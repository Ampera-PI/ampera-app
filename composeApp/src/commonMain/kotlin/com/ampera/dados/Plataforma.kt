package com.ampera.dados

expect fun urlDaApi(): String

expect fun lerToken(): String?

expect fun gravarToken(token: String?)
