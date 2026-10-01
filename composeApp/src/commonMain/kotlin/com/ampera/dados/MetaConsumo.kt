package com.ampera.dados

import kotlinx.serialization.Serializable

@Serializable
data class MetaConsumo(
    val id: Long,
    val residenciaId: Long,
    val limiteKwh: Double,
    val inicio: String,
    val fim: String,
    val consumoKwh: Double = 0.0,
    val titulo: String = "Meta de consumo",
    val ativa: Boolean = true,
)

@Serializable
data class MetaConsumoForm(
    val residenciaId: Long,
    val limiteKwh: Double,
    val inicio: String,
    val fim: String,
    val titulo: String = "Meta de consumo",
    val ativa: Boolean = true,
)
