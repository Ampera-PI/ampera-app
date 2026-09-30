package com.ampera.dados

import kotlinx.serialization.Serializable

@Serializable
data class Alerta(
    val id: Long,
    val dispositivoId: Long,
    val grandeza: String,
    val limiar: Double,
    val mensagem: String,
    val ativo: Boolean,
    val disparado: Boolean,
)

@Serializable
data class AlertaForm(
    val dispositivoId: Long,
    val grandeza: String,
    val limiar: Double,
    val mensagem: String,
    val ativo: Boolean,
)
