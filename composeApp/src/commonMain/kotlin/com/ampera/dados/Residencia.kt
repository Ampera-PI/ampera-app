package com.ampera.dados

import kotlinx.serialization.Serializable

@Serializable
data class Residencia(
    val id: Long,
    val usuarioId: Long,
    val nome: String,
    val endereco: String,
    val cidade: String = "",
    val areaM2: Double = 0.0,
    val numeroMoradores: Int = 1,
)

@Serializable
data class ResidenciaForm(
    val nome: String,
    val endereco: String,
    val cidade: String,
    val areaM2: Double = 0.0,
    val numeroMoradores: Int = 1,
)
