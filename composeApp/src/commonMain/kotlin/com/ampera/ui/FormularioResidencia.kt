package com.ampera.ui

import androidx.compose.runtime.Composable

@Composable
fun FormularioResidencia(cliente: ClienteRecurso, estado: CrudEstado, modelo: CrudViewModel) =
    FormularioBase(cliente, estado, modelo) {
        CamposDo(cliente, estado, modelo, "nome", "endereco", "cidade", "areaM2", "numeroMoradores")
    }
