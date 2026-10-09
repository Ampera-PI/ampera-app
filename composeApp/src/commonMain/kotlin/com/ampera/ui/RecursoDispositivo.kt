package com.ampera.ui

import com.ampera.dados.AmperaRepositorio
import com.ampera.dados.DispositivoForm

internal fun recursoDispositivo(repo: AmperaRepositorio) = recurso(
    titulo = "Dispositivos",
    singular = "Dispositivo",
    formulario = { c, e, m -> FormularioDispositivo(c, e, m) },
    campos = { catalogo ->
        listOf(
            Campo("comodoId", "Cômodo", TipoCampo.SELECAO, catalogo.comodos),
            Campo("nome", "Nome"),
            Campo("codigoMqtt", "Código MQTT (DEVICE_ID do ESP32)"),
            Campo("fabricante", "Fabricante"),
            Campo("potenciaNominalW", "Potência nominal (W)", TipoCampo.NUMERO),
            Campo("instalacao", "Data de instalação (opcional)", TipoCampo.DATA),
            Campo("ativo", "Dispositivo ativo", TipoCampo.BOOLEANO),
        )
    },
    iniciais = { catalogo -> primeiro("comodoId", catalogo.comodos) + mapOf("ativo" to "true") },
    listar = { catalogo ->
        repo.listarDispositivos().map {
            ItemLista(
                it.id,
                it.nome,
                "${catalogo.comodo(it.comodoId)} · ${it.fabricante.ifBlank { "sem fabricante" }} · ${numero(it.potenciaNominalW, 0)} W",
                mapOf(
                    "comodoId" to it.comodoId.toString(),
                    "nome" to it.nome,
                    "codigoMqtt" to it.codigoMqtt,
                    "fabricante" to it.fabricante,
                    "potenciaNominalW" to it.potenciaNominalW.toString(),
                    "instalacao" to it.instalacao.orEmpty(),
                    "ativo" to it.ativo.toString(),
                ),
                etiqueta = it.codigoMqtt,
            )
        }
    },
    salvar = { id, valores ->
        val form = DispositivoForm(
            valores.longo("comodoId", "o cômodo"),
            valores.texto("nome", "Nome"),
            valores.texto("codigoMqtt", "Código MQTT"),
            valores["fabricante"]?.trim().orEmpty(),
            valores.numero("potenciaNominalW"),
            valores.dataOpcional("instalacao"),
            valores.booleano("ativo"),
        )
        if (id == null) repo.criarDispositivo(form) else repo.atualizarDispositivo(id, form)
    },
    excluir = { repo.excluirDispositivo(it) },
)
