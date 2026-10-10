package com.ampera.ui

import com.ampera.dados.AmperaRepositorio
import com.ampera.dados.MedicaoForm

internal fun recursoMedicao(repo: AmperaRepositorio) = recurso(
    titulo = "Medições",
    singular = "Medição",
    formulario = { c, e, m -> FormularioMedicao(c, e, m) },
    campos = { catalogo ->
        listOf(
            Campo("dispositivoId", "Dispositivo", TipoCampo.SELECAO, catalogo.dispositivos),
            Campo("tensao", "Tensão (V)", TipoCampo.NUMERO),
            Campo("corrente", "Corrente (A)", TipoCampo.NUMERO),
            Campo("potencia", "Potência (W)", TipoCampo.NUMERO),
            Campo("energiaKwh", "Energia acumulada (kWh)", TipoCampo.NUMERO),
            Campo("observacao", "Observação"),
            Campo("validada", "Medição validada", TipoCampo.BOOLEANO),
        )
    },
    iniciais = { catalogo -> primeiro("dispositivoId", catalogo.dispositivos) + mapOf("validada" to "true") },
    listar = { catalogo ->
        repo.listarMedicoes(null, 50).map {
            ItemLista(
                it.id,
                "${numero(it.potencia, 1)} W · ${numero(it.tensao, 1)} V · ${numero(it.corrente, 3)} A",
                "${catalogo.dispositivo(it.dispositivoId)} · ${numero(it.energiaKwh, 4)} kWh · R$ ${numero(it.custo, 4)} · ${dataHora(it.instante)}",
                mapOf(
                    "dispositivoId" to it.dispositivoId.toString(),
                    "tensao" to it.tensao.toString(),
                    "corrente" to it.corrente.toString(),
                    "potencia" to it.potencia.toString(),
                    "energiaKwh" to it.energiaKwh.toString(),
                    "observacao" to it.observacao,
                    "validada" to it.validada.toString(),
                ),
                etiqueta = it.origem,
            )
        }
    },
    salvar = { id, valores ->
        val form = MedicaoForm(
            valores.longo("dispositivoId", "o dispositivo"),
            valores.numero("tensao"),
            valores.numero("corrente"),
            valores.numero("potencia"),
            valores.numero("energiaKwh"),
            valores["observacao"]?.trim().orEmpty(),
            valores.booleano("validada"),
        )
        if (id == null) repo.criarMedicao(form) else repo.atualizarMedicao(id, form)
    },
    excluir = { repo.excluirMedicao(it) },
)
