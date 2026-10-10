package com.ampera.ui

import androidx.compose.runtime.Composable

@Composable
fun FormularioMedicao(cliente: ClienteRecurso, estado: CrudEstado, modelo: CrudViewModel) =
    FormularioBase(cliente, estado, modelo) {
        CamposDo(cliente, estado, modelo, "dispositivoId", "tensao", "corrente", "potencia", "energiaKwh", "observacao", "validada")
    }
