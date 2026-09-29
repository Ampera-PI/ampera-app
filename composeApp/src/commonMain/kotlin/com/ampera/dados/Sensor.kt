package com.ampera.dados

import kotlinx.serialization.Serializable

@Serializable
data class Sensor(
    val id: Long,
    val dispositivoId: Long,
    val tipo: String,
    val unidade: String,
    val modelo: String = "",
    val precisao: Double = 0.0,
    val calibradoEm: String? = null,
    val ativo: Boolean = true,
)

@Serializable
data class SensorForm(
    val dispositivoId: Long,
    val tipo: String,
    val unidade: String,
    val modelo: String = "",
    val precisao: Double = 0.0,
    val calibradoEm: String? = null,
    val ativo: Boolean = true,
)
