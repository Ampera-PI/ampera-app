package com.ampera.ui

import com.ampera.dados.AmperaRepositorio
import com.ampera.dados.GerarRelatorioForm
import com.ampera.dados.RelatorioForm

internal fun recursoRelatorio(repo: AmperaRepositorio) = recurso(
    titulo = "Relatórios",
    singular = "Relatório",
    formulario = { c, e, m -> FormularioRelatorio(c, e, m) },
    acaoExtra = "Gerar a partir das medições (com previsão)",
    campos = { catalogo ->
        listOf(
            Campo("residenciaId", "Residência", TipoCampo.SELECAO, catalogo.residencias),
            Campo("titulo", "Título"),
            Campo("inicio", "Início", TipoCampo.DATA),
            Campo("fim", "Fim", TipoCampo.DATA),
            Campo("consumoKwh", "Consumo (kWh)", TipoCampo.NUMERO),
            Campo("custoTotal", "Custo total (R$)", TipoCampo.NUMERO),
            Campo("previsaoKwh", "Previsão próximo dia (kWh)", TipoCampo.NUMERO),
        )
    },
    iniciais = { catalogo ->
        primeiro("residenciaId", catalogo.residencias) + mapOf("titulo" to "Relatório mensal", "inicio" to inicioDoMes(), "fim" to hoje())
    },
    listar = { catalogo ->
        repo.listarRelatorios().map {
            ItemLista(
                it.id,
                it.titulo,
                "${numero(it.consumoKwh, 4)} kWh · R$ ${numero(it.custoTotal, 2)} · ${catalogo.residencia(it.residenciaId)} · ${it.inicio} a ${it.fim}",
                mapOf(
                    "residenciaId" to it.residenciaId.toString(),
                    "titulo" to it.titulo,
                    "inicio" to it.inicio,
                    "fim" to it.fim,
                    "consumoKwh" to it.consumoKwh.toString(),
                    "custoTotal" to it.custoTotal.toString(),
                    "previsaoKwh" to it.previsaoKwh.toString(),
                ),
                etiqueta = "previsão ${numero(it.previsaoKwh, 4)} kWh",
            )
        }
    },
    salvar = { id, valores ->
        val form = RelatorioForm(
            valores.longo("residenciaId", "a residência"),
            valores.texto("inicio", "Início"),
            valores.texto("fim", "Fim"),
            valores.numero("consumoKwh"),
            valores.numero("custoTotal"),
            valores.numero("previsaoKwh"),
            valores.texto("titulo", "Título"),
        )
        if (id == null) repo.criarRelatorio(form) else repo.atualizarRelatorio(id, form)
    },
    excluir = { repo.excluirRelatorio(it) },
    extra = { valores ->
        repo.gerarRelatorio(
            GerarRelatorioForm(
                valores.longo("residenciaId", "a residência"),
                valores.texto("inicio", "Início"),
                valores.texto("fim", "Fim"),
                valores["titulo"]?.trim().orEmpty().ifBlank { "Relatório gerado" },
            ),
        )
    },
)
