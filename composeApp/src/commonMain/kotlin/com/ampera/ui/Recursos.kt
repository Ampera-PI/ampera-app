package com.ampera.ui

import com.ampera.dados.AmperaRepositorio

fun recursosCasa(repo: AmperaRepositorio) = listOf(recursoResidencia(repo), recursoComodo(repo), recursoDispositivo(repo))

fun recursosEnergia(repo: AmperaRepositorio) = listOf(recursoSensor(repo), recursoMedicao(repo), recursoTarifa(repo), recursoAlerta(repo))

fun recursosConta(repo: AmperaRepositorio) = listOf(recursoUsuario(repo), recursoMeta(repo), recursoRelatorio(repo))
