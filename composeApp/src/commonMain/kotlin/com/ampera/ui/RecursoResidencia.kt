package com.ampera.ui

import com.ampera.dados.AmperaRepositorio
import com.ampera.dados.ResidenciaForm

internal fun recursoResidencia(repo: AmperaRepositorio) = recurso(
    titulo = "Residências",
    singular = "Residência",
    formulario = { c, e, m -> FormularioResidencia(c, e, m) },
    campos = {
        listOf(
            Campo("nome", "Nome"),
            Campo("endereco", "Endereço"),
            Campo("cidade", "Cidade"),
            Campo("areaM2", "Área (m²)", TipoCampo.NUMERO),
            Campo("numeroMoradores", "Número de moradores", TipoCampo.NUMERO),
        )
    },
    iniciais = { mapOf("numeroMoradores" to "1") },
    listar = {
        repo.listarResidencias().map {
            ItemLista(
                it.id,
                it.nome,
                "${it.endereco} · ${it.cidade} · ${numero(it.areaM2, 1)} m² · ${it.numeroMoradores} morador(es)",
                mapOf(
                    "nome" to it.nome,
                    "endereco" to it.endereco,
                    "cidade" to it.cidade,
                    "areaM2" to it.areaM2.toString(),
                    "numeroMoradores" to it.numeroMoradores.toString(),
                ),
            )
        }
    },
    salvar = { id, valores ->
        val form = ResidenciaForm(
            valores.texto("nome", "Nome"),
            valores.texto("endereco", "Endereço"),
            valores.texto("cidade", "Cidade"),
            valores.numero("areaM2"),
            valores.inteiro("numeroMoradores", 1),
        )
        if (id == null) repo.criarResidencia(form) else repo.atualizarResidencia(id, form)
    },
    excluir = { repo.excluirResidencia(it) },
)
