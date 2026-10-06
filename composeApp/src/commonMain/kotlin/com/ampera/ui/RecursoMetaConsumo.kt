package com.ampera.ui

import com.ampera.dados.AmperaRepositorio
import com.ampera.dados.MetaConsumoForm

internal fun recursoMeta(repo: AmperaRepositorio) = recurso(
    titulo = "Metas",
    singular = "Meta de consumo",
    formulario = { c, e, m -> FormularioMeta(c, e, m) },
    campos = { catalogo ->
        listOf(
            Campo("residenciaId", "Residência", TipoCampo.SELECAO, catalogo.residencias),
            Campo("titulo", "Título"),
            Campo("limiteKwh", "Limite (kWh)", TipoCampo.NUMERO),
            Campo("inicio", "Início", TipoCampo.DATA),
            Campo("fim", "Fim", TipoCampo.DATA),
            Campo("ativa", "Meta ativa", TipoCampo.BOOLEANO),
        )
    },
    iniciais = { catalogo -> primeiro("residenciaId", catalogo.residencias) + mapOf("inicio" to hoje(), "ativa" to "true") },
    listar = { catalogo ->
        repo.listarMetas().map {
            val uso = if (it.limiteKwh > 0) it.consumoKwh / it.limiteKwh * 100 else 0.0
            ItemLista(
                it.id,
                it.titulo,
                "${numero(it.consumoKwh, 3)} de ${numero(it.limiteKwh, 1)} kWh · ${catalogo.residencia(it.residenciaId)} · ${it.inicio} a ${it.fim}",
                mapOf(
                    "residenciaId" to it.residenciaId.toString(),
                    "titulo" to it.titulo,
                    "limiteKwh" to it.limiteKwh.toString(),
                    "inicio" to it.inicio,
                    "fim" to it.fim,
                    "ativa" to it.ativa.toString(),
                ),
                etiqueta = if (it.ativa) "${numero(uso, 1)}%" else "inativa",
                destaque = it.ativa && uso >= 90,
            )
        }
    },
    salvar = { id, valores ->
        val form = MetaConsumoForm(
            valores.longo("residenciaId", "a residência"),
            valores.numero("limiteKwh"),
            valores.texto("inicio", "Início"),
            valores.texto("fim", "Fim"),
            valores.texto("titulo", "Título"),
            valores.booleano("ativa"),
        )
        if (id == null) repo.criarMeta(form) else repo.atualizarMeta(id, form)
    },
    excluir = { repo.excluirMeta(it) },
)
