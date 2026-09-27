package com.ampera.dados

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: Long,
    val nome: String,
    val email: String,
    val telefone: String = "",
    val dataNascimento: String? = null,
    val ativo: Boolean = true,
)

@Serializable
data class UsuarioForm(
    val nome: String,
    val email: String,
    val senha: String? = null,
    val telefone: String = "",
    val dataNascimento: String? = null,
    val ativo: Boolean = true,
)
