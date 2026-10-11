package com.ampera.ui

import com.ampera.dados.AlertaForm
import com.ampera.dados.AmperaRepositorio

internal val grandezas = listOf("POTENCIA" to "Potência (W)", "TENSAO" to "Tensão (V)", "CORRENTE" to "Corrente (A)")

internal fun recursoAlerta(repo: AmperaRepositorio) = recurso(
    titulo = "Alertas",
    singular = "Alerta",
    formulario = { c, e, m -> FormularioAlerta(c, e, m) },
    campos = { catalogo ->
        listOf(
            Campo("dispositivoId", "Dispositivo", TipoCampo.SELECAO, catalogo.dispositivos),
            Campo("grandeza", "Grandeza", TipoCampo.SELECAO, grandezas),
            Campo("limiar", "Disparar acima de", TipoCampo.NUMERO),
            Campo("mensagem", "Mensagem"),
            Campo("ativo", "Alerta ativo", TipoCampo.BOOLEANO),
        )
    },
    iniciais = { catalogo -> primeiro("dispositivoId", catalogo.dispositivos) + mapOf("grandeza" to "POTENCIA", "ativo" to "true") },
    listar = { catalogo ->
        repo.listarAlertas().map {
            ItemLista(
                it.id,
                it.mensagem,
                "${catalogo.dispositivo(it.dispositivoId)} · ${grandezas.toMap()[it.grandeza] ?: it.grandeza} acima de ${numero(it.limiar, 1)}",
                mapOf(
                    "dispositivoId" to it.dispositivoId.toString(),
                    "grandeza" to it.grandeza,
                    "limiar" to it.limiar.toString(),
                    "mensagem" to it.mensagem,
                    "ativo" to it.ativo.toString(),
                ),
                etiqueta = when {
                    !it.ativo -> "inativo"
                    it.disparado -> "disparado"
                    else -> "normal"
                },
                destaque = it.ativo && it.disparado,
            )
        }
    },
    salvar = { id, valores ->
        val form = AlertaForm(
            valores.longo("dispositivoId", "o dispositivo"),
            valores.texto("grandeza", "Grandeza"),
            valores.numero("limiar"),
            valores.texto("mensagem", "Mensagem"),
            valores.booleano("ativo"),
        )
        if (id == null) repo.criarAlerta(form) else repo.atualizarAlerta(id, form)
    },
    excluir = { repo.excluirAlerta(it) },
)
