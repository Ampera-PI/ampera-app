package com.ampera.ui

import androidx.compose.runtime.Composable

@Composable
fun FormularioComodo(cliente: ClienteRecurso, estado: CrudEstado, modelo: CrudViewModel) =
    FormularioBase(cliente, estado, modelo) {
        CamposDo(cliente, estado, modelo, "residenciaId", "nome", "tipo", "andar", "areaM2", "ativo")
    }
