package com.ampera.ui

import androidx.compose.runtime.Composable

@Composable
fun FormularioDispositivo(cliente: ClienteRecurso, estado: CrudEstado, modelo: CrudViewModel) =
    FormularioBase(cliente, estado, modelo) {
        CamposDo(cliente, estado, modelo, "comodoId", "nome", "codigoMqtt", "fabricante", "potenciaNominalW", "instalacao", "ativo")
    }
