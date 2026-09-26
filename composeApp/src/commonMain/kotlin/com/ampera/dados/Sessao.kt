package com.ampera.dados

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

@Serializable
data class ErroApi(val mensagem: String? = null)

@Serializable
data class LoginPedido(val email: String, val senha: String)

@Serializable
data class LoginResposta(val token: String, val usuario: Usuario)

class ErroAmpera(mensagem: String) : RuntimeException(mensagem)

class Sessao {
    private val _token = MutableStateFlow(lerToken())
    val token: StateFlow<String?> = _token

    fun iniciar(token: String) {
        gravarToken(token)
        _token.value = token
    }

    fun encerrar() {
        gravarToken(null)
        _token.value = null
    }
}
