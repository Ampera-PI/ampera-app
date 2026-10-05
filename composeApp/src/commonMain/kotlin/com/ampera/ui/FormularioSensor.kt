package com.ampera.ui

import androidx.compose.runtime.Composable

@Composable
fun FormularioSensor(cliente: ClienteRecurso, estado: CrudEstado, modelo: CrudViewModel) =
    FormularioBase(cliente, estado, modelo) {
        CamposDo(cliente, estado, modelo, "dispositivoId", "tipo", "unidade", "modelo", "precisao", "calibradoEm", "ativo")
    }
