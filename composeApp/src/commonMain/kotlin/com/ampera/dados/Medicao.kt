package com.ampera.dados

import kotlinx.serialization.Serializable

@Serializable
data class Medicao(
    val id: Long,
    val dispositivoId: Long,
    val tensao: Double,
    val corrente: Double,
    val potencia: Double,
    val energiaKwh: Double,
    val custo: Double,
    val instante: String,
    val origem: String,
    val observacao: String = "",
    val validada: Boolean = true,
)

@Serializable
data class MedicaoForm(
    val dispositivoId: Long,
    val tensao: Double,
    val corrente: Double,
    val potencia: Double,
    val energiaKwh: Double,
    val observacao: String = "",
    val validada: Boolean = true,
)
