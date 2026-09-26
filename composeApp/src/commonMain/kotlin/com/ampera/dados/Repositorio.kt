package com.ampera.dados

interface AutenticacaoRepositorio {
    suspend fun login(email: String, senha: String): LoginResposta
    suspend fun registrar(form: UsuarioForm): Usuario
}

interface AmperaRepositorio : AutenticacaoRepositorio {
    suspend fun listarUsuarios(): List<Usuario>
    suspend fun criarUsuario(form: UsuarioForm): Usuario
    suspend fun atualizarUsuario(id: Long, form: UsuarioForm): Usuario
    suspend fun excluirUsuario(id: Long)

    suspend fun listarResidencias(): List<Residencia>
    suspend fun criarResidencia(form: ResidenciaForm): Residencia
    suspend fun atualizarResidencia(id: Long, form: ResidenciaForm): Residencia
    suspend fun excluirResidencia(id: Long)

    suspend fun listarComodos(): List<Comodo>
    suspend fun criarComodo(form: ComodoForm): Comodo
    suspend fun atualizarComodo(id: Long, form: ComodoForm): Comodo
    suspend fun excluirComodo(id: Long)

    suspend fun listarDispositivos(): List<Dispositivo>
    suspend fun criarDispositivo(form: DispositivoForm): Dispositivo
    suspend fun atualizarDispositivo(id: Long, form: DispositivoForm): Dispositivo
    suspend fun excluirDispositivo(id: Long)

    suspend fun listarSensores(): List<Sensor>
    suspend fun criarSensor(form: SensorForm): Sensor
    suspend fun atualizarSensor(id: Long, form: SensorForm): Sensor
    suspend fun excluirSensor(id: Long)

    suspend fun listarMedicoes(dispositivoId: Long? = null, limite: Int = 40): List<Medicao>
    suspend fun criarMedicao(form: MedicaoForm): Medicao
    suspend fun atualizarMedicao(id: Long, form: MedicaoForm): Medicao
    suspend fun excluirMedicao(id: Long)

    suspend fun listarTarifas(): List<Tarifa>
    suspend fun criarTarifa(form: TarifaForm): Tarifa
    suspend fun atualizarTarifa(id: Long, form: TarifaForm): Tarifa
    suspend fun excluirTarifa(id: Long)

    suspend fun listarAlertas(): List<Alerta>
    suspend fun criarAlerta(form: AlertaForm): Alerta
    suspend fun atualizarAlerta(id: Long, form: AlertaForm): Alerta
    suspend fun excluirAlerta(id: Long)

    suspend fun listarMetas(): List<MetaConsumo>
    suspend fun criarMeta(form: MetaConsumoForm): MetaConsumo
    suspend fun atualizarMeta(id: Long, form: MetaConsumoForm): MetaConsumo
    suspend fun excluirMeta(id: Long)

    suspend fun listarRelatorios(): List<Relatorio>
    suspend fun criarRelatorio(form: RelatorioForm): Relatorio
    suspend fun atualizarRelatorio(id: Long, form: RelatorioForm): Relatorio
    suspend fun excluirRelatorio(id: Long)
    suspend fun gerarRelatorio(form: GerarRelatorioForm): Relatorio
}
