package com.ampera.dados

import kotlinx.serialization.Serializable

@Serializable
data class Comodo(
    val id: Long,
    val residenciaId: Long,
    val nome: String,
    val tipo: String = "Outro",
    val andar: Int = 0,
    val areaM2: Double = 0.0,
    val ativo: Boolean = true,
)

@Serializable
data class ComodoForm(
    val residenciaId: Long,
    val nome: String,
    val tipo: String = "Outro",
    val andar: Int = 0,
    val areaM2: Double = 0.0,
    val ativo: Boolean = true,
)
