package com.ampera.ui

import androidx.compose.runtime.Composable

@Composable
fun FormularioUsuario(cliente: ClienteRecurso, estado: CrudEstado, modelo: CrudViewModel) =
    FormularioBase(cliente, estado, modelo) {
        CamposDo(cliente, estado, modelo, "nome", "email", "senha", "telefone", "dataNascimento", "ativo")
    }
