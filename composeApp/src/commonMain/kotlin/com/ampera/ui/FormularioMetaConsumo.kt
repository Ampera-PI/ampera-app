package com.ampera.ui

import androidx.compose.runtime.Composable

@Composable
fun FormularioMeta(cliente: ClienteRecurso, estado: CrudEstado, modelo: CrudViewModel) =
    FormularioBase(cliente, estado, modelo) {
        CamposDo(cliente, estado, modelo, "residenciaId", "titulo", "limiteKwh", "inicio", "fim", "ativa")
    }
