package com.ampera.ui

import com.ampera.dados.AmperaRepositorio
import com.ampera.dados.TarifaForm

internal val bandeiras = listOf("VERDE" to "Verde", "AMARELA" to "Amarela", "VERMELHA" to "Vermelha")

internal fun recursoTarifa(repo: AmperaRepositorio) = recurso(
    titulo = "Tarifas",
    singular = "Tarifa",
    formulario = { c, e, m -> FormularioTarifa(c, e, m) },
    campos = { catalogo ->
        listOf(
            Campo("residenciaId", "Residência", TipoCampo.SELECAO, catalogo.residencias),
            Campo("distribuidora", "Distribuidora"),
            Campo("bandeira", "Bandeira", TipoCampo.SELECAO, bandeiras),
            Campo("valorKwh", "R$ por kWh", TipoCampo.NUMERO),
            Campo("vigenciaInicio", "Início da vigência", TipoCampo.DATA),
            Campo("vigenciaFim", "Fim da vigência (opcional)", TipoCampo.DATA),
            Campo("ativa", "Tarifa ativa", TipoCampo.BOOLEANO),
        )
    },
    iniciais = { catalogo ->
        primeiro("residenciaId", catalogo.residencias) + mapOf("vigenciaInicio" to hoje(), "bandeira" to "VERDE", "ativa" to "true")
    },
    listar = { catalogo ->
        repo.listarTarifas().map {
            ItemLista(
                it.id,
                "R$ ${numero(it.valorKwh, 4)} / kWh",
                "${catalogo.residencia(it.residenciaId)} · ${it.distribuidora.ifBlank { "sem distribuidora" }} · bandeira ${it.bandeira.lowercase()} · desde ${it.vigenciaInicio}${it.vigenciaFim?.let { fim -> " até $fim" }.orEmpty()}",
                mapOf(
                    "residenciaId" to it.residenciaId.toString(),
                    "distribuidora" to it.distribuidora,
                    "bandeira" to it.bandeira,
                    "valorKwh" to it.valorKwh.toString(),
                    "vigenciaInicio" to it.vigenciaInicio,
                    "vigenciaFim" to it.vigenciaFim.orEmpty(),
                    "ativa" to it.ativa.toString(),
                ),
                etiqueta = when {
                    !it.ativa -> "inativa"
                    it.vigenciaFim == null -> "em vigor"
                    else -> null
                },
            )
        }
    },
    salvar = { id, valores ->
        val form = TarifaForm(
            valores.longo("residenciaId", "a residência"),
            valores.numero("valorKwh"),
            valores.texto("vigenciaInicio", "Início da vigência"),
            valores.dataOpcional("vigenciaFim"),
            valores["distribuidora"]?.trim().orEmpty(),
            valores.texto("bandeira", "Bandeira"),
            valores.booleano("ativa"),
        )
        if (id == null) repo.criarTarifa(form) else repo.atualizarTarifa(id, form)
    },
    excluir = { repo.excluirTarifa(it) },
)
