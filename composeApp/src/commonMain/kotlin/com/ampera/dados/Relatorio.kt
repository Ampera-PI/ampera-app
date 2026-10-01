package com.ampera.dados

import kotlinx.serialization.Serializable

@Serializable
data class Relatorio(
    val id: Long,
    val residenciaId: Long,
    val titulo: String = "Relatório",
    val inicio: String,
    val fim: String,
    val consumoKwh: Double,
    val custoTotal: Double,
    val previsaoKwh: Double,
    val criadoEm: String,
)

@Serializable
data class RelatorioForm(
    val residenciaId: Long,
    val inicio: String,
    val fim: String,
    val consumoKwh: Double = 0.0,
    val custoTotal: Double = 0.0,
    val previsaoKwh: Double = 0.0,
    val titulo: String = "Relatório",
)

@Serializable
data class GerarRelatorioForm(val residenciaId: Long, val inicio: String, val fim: String, val titulo: String = "Relatório gerado")
