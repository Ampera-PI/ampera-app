package com.ampera.ui

import com.ampera.dados.AmperaRepositorio
import com.ampera.dados.ComodoForm

internal fun recursoComodo(repo: AmperaRepositorio) = recurso(
    titulo = "Cômodos",
    singular = "Cômodo",
    formulario = { c, e, m -> FormularioComodo(c, e, m) },
    campos = { catalogo ->
        listOf(
            Campo("residenciaId", "Residência", TipoCampo.SELECAO, catalogo.residencias),
            Campo("nome", "Nome"),
            Campo("tipo", "Tipo (sala, quarto, cozinha...)"),
            Campo("andar", "Andar", TipoCampo.NUMERO),
            Campo("areaM2", "Área (m²)", TipoCampo.NUMERO),
            Campo("ativo", "Cômodo ativo", TipoCampo.BOOLEANO),
        )
    },
    iniciais = { catalogo -> primeiro("residenciaId", catalogo.residencias) + mapOf("tipo" to "Outro", "andar" to "0", "ativo" to "true") },
    listar = { catalogo ->
        repo.listarComodos().map {
            ItemLista(
                it.id,
                it.nome,
                "${catalogo.residencia(it.residenciaId)} · ${it.tipo} · andar ${it.andar} · ${numero(it.areaM2, 1)} m²",
                mapOf(
                    "residenciaId" to it.residenciaId.toString(),
                    "nome" to it.nome,
                    "tipo" to it.tipo,
                    "andar" to it.andar.toString(),
                    "areaM2" to it.areaM2.toString(),
                    "ativo" to it.ativo.toString(),
                ),
                etiqueta = if (it.ativo) null else "inativo",
            )
        }
    },
    salvar = { id, valores ->
        val form = ComodoForm(
            valores.longo("residenciaId", "a residência"),
            valores.texto("nome", "Nome"),
            valores.texto("tipo", "Tipo"),
            valores.inteiro("andar", 0),
            valores.numero("areaM2"),
            valores.booleano("ativo"),
        )
        if (id == null) repo.criarComodo(form) else repo.atualizarComodo(id, form)
    },
    excluir = { repo.excluirComodo(it) },
)
