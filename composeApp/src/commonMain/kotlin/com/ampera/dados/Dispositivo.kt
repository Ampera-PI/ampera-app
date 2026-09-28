package com.ampera.dados

import kotlinx.serialization.Serializable

@Serializable
data class Dispositivo(
    val id: Long,
    val comodoId: Long,
    val nome: String,
    val codigoMqtt: String,
    val fabricante: String = "",
    val potenciaNominalW: Double = 0.0,
    val instalacao: String? = null,
    val ativo: Boolean = true,
)

@Serializable
data class DispositivoForm(
    val comodoId: Long,
    val nome: String,
    val codigoMqtt: String,
    val fabricante: String = "",
    val potenciaNominalW: Double = 0.0,
    val instalacao: String? = null,
    val ativo: Boolean = true,
)
