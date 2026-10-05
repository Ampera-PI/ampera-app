package com.ampera.ui

import androidx.compose.runtime.Composable

@Composable
fun FormularioTarifa(cliente: ClienteRecurso, estado: CrudEstado, modelo: CrudViewModel) =
    FormularioBase(cliente, estado, modelo) {
        CamposDo(cliente, estado, modelo, "residenciaId", "distribuidora", "bandeira", "valorKwh", "vigenciaInicio", "vigenciaFim", "ativa")
    }
