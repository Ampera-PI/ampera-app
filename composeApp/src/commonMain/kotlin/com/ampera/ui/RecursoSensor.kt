package com.ampera.ui

import com.ampera.dados.AmperaRepositorio
import com.ampera.dados.SensorForm

internal val tiposSensor = listOf("TENSAO" to "Tensão", "CORRENTE" to "Corrente")

internal fun recursoSensor(repo: AmperaRepositorio) = recurso(
    titulo = "Sensores",
    singular = "Sensor",
    formulario = { c, e, m -> FormularioSensor(c, e, m) },
    campos = { catalogo ->
        listOf(
            Campo("dispositivoId", "Dispositivo", TipoCampo.SELECAO, catalogo.dispositivos),
            Campo("tipo", "Tipo", TipoCampo.SELECAO, tiposSensor),
            Campo("unidade", "Unidade"),
            Campo("modelo", "Modelo (ZMPT101B, SCT-013...)"),
            Campo("precisao", "Precisão (%)", TipoCampo.NUMERO),
            Campo("calibradoEm", "Calibrado em (opcional)", TipoCampo.DATA),
            Campo("ativo", "Sensor ativo", TipoCampo.BOOLEANO),
        )
    },
    iniciais = { catalogo ->
        primeiro("dispositivoId", catalogo.dispositivos) + mapOf("tipo" to "TENSAO", "unidade" to "V", "ativo" to "true")
    },
    listar = { catalogo ->
        repo.listarSensores().map {
            ItemLista(
                it.id,
                tiposSensor.toMap()[it.tipo] ?: it.tipo,
                "${catalogo.dispositivo(it.dispositivoId)} · ${it.modelo.ifBlank { "sem modelo" }} · precisão ${numero(it.precisao, 1)}%",
                mapOf(
                    "dispositivoId" to it.dispositivoId.toString(),
                    "tipo" to it.tipo,
                    "unidade" to it.unidade,
                    "modelo" to it.modelo,
                    "precisao" to it.precisao.toString(),
                    "calibradoEm" to it.calibradoEm.orEmpty(),
                    "ativo" to it.ativo.toString(),
                ),
                etiqueta = it.unidade,
            )
        }
    },
    salvar = { id, valores ->
        val form = SensorForm(
            valores.longo("dispositivoId", "o dispositivo"),
            valores.texto("tipo", "Tipo"),
            valores.texto("unidade", "Unidade"),
            valores["modelo"]?.trim().orEmpty(),
            valores.numero("precisao"),
            valores.dataOpcional("calibradoEm"),
            valores.booleano("ativo"),
        )
        if (id == null) repo.criarSensor(form) else repo.atualizarSensor(id, form)
    },
    excluir = { repo.excluirSensor(it) },
)
