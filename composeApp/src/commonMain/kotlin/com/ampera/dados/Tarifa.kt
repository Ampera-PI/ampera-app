package com.ampera.dados

import kotlinx.serialization.Serializable

@Serializable
data class Tarifa(
    val id: Long,
    val residenciaId: Long,
    val valorKwh: Double,
    val vigenciaInicio: String,
    val vigenciaFim: String? = null,
    val distribuidora: String = "",
    val bandeira: String = "VERDE",
    val ativa: Boolean = true,
)

@Serializable
data class TarifaForm(
    val residenciaId: Long,
    val valorKwh: Double,
    val vigenciaInicio: String,
    val vigenciaFim: String? = null,
    val distribuidora: String = "",
    val bandeira: String = "VERDE",
    val ativa: Boolean = true,
)
