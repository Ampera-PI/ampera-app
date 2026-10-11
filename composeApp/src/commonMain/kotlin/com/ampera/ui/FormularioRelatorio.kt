package com.ampera.ui

import androidx.compose.runtime.Composable

@Composable
fun FormularioRelatorio(cliente: ClienteRecurso, estado: CrudEstado, modelo: CrudViewModel) =
    FormularioBase(cliente, estado, modelo) {
        CamposDo(cliente, estado, modelo, "residenciaId", "titulo", "inicio", "fim", "consumoKwh", "custoTotal", "previsaoKwh")
    }
