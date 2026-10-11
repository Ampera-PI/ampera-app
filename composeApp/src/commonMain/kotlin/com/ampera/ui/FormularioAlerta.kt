package com.ampera.ui

import androidx.compose.runtime.Composable

@Composable
fun FormularioAlerta(cliente: ClienteRecurso, estado: CrudEstado, modelo: CrudViewModel) =
    FormularioBase(cliente, estado, modelo) {
        CamposDo(cliente, estado, modelo, "dispositivoId", "grandeza", "limiar", "mensagem", "ativo")
    }
