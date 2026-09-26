package com.ampera.dados

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AmperaApi(
    private val base: String,
    private val sessao: Sessao,
) : AmperaRepositorio {
    private val json = Json { ignoreUnknownKeys = true }

    private val http = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) { json(json) }
        defaultRequest {
            contentType(ContentType.Application.Json)
            sessao.token.value?.let { bearerAuth(it) }
        }
    }

    override suspend fun login(email: String, senha: String): LoginResposta = tratar {
        http.post("$base/api/auth/signin") { setBody(LoginPedido(email, senha)) }.body()
    }

    override suspend fun registrar(form: UsuarioForm): Usuario = tratar {
        http.post("$base/api/auth/signup") { setBody(form) }.body()
    }

    override suspend fun listarUsuarios(): List<Usuario> = get("/api/usuarios")
    override suspend fun criarUsuario(form: UsuarioForm): Usuario = post("/api/usuarios", form)
    override suspend fun atualizarUsuario(id: Long, form: UsuarioForm): Usuario = put("/api/usuarios/$id", form)
    override suspend fun excluirUsuario(id: Long) = delete("/api/usuarios/$id")

    override suspend fun listarResidencias(): List<Residencia> = get("/api/residencias")
    override suspend fun criarResidencia(form: ResidenciaForm): Residencia = post("/api/residencias", form)
    override suspend fun atualizarResidencia(id: Long, form: ResidenciaForm): Residencia = put("/api/residencias/$id", form)
    override suspend fun excluirResidencia(id: Long) = delete("/api/residencias/$id")

    override suspend fun listarComodos(): List<Comodo> = get("/api/comodos")
    override suspend fun criarComodo(form: ComodoForm): Comodo = post("/api/comodos", form)
    override suspend fun atualizarComodo(id: Long, form: ComodoForm): Comodo = put("/api/comodos/$id", form)
    override suspend fun excluirComodo(id: Long) = delete("/api/comodos/$id")

    override suspend fun listarDispositivos(): List<Dispositivo> = get("/api/dispositivos")
    override suspend fun criarDispositivo(form: DispositivoForm): Dispositivo = post("/api/dispositivos", form)
    override suspend fun atualizarDispositivo(id: Long, form: DispositivoForm): Dispositivo = put("/api/dispositivos/$id", form)
    override suspend fun excluirDispositivo(id: Long) = delete("/api/dispositivos/$id")

    override suspend fun listarSensores(): List<Sensor> = get("/api/sensores")
    override suspend fun criarSensor(form: SensorForm): Sensor = post("/api/sensores", form)
    override suspend fun atualizarSensor(id: Long, form: SensorForm): Sensor = put("/api/sensores/$id", form)
    override suspend fun excluirSensor(id: Long) = delete("/api/sensores/$id")

    override suspend fun listarMedicoes(dispositivoId: Long?, limite: Int): List<Medicao> {
        val filtro = dispositivoId?.let { "&dispositivoId=$it" }.orEmpty()
        return get("/api/medicoes?limit=$limite$filtro")
    }

    override suspend fun criarMedicao(form: MedicaoForm): Medicao = post("/api/medicoes", form)
    override suspend fun atualizarMedicao(id: Long, form: MedicaoForm): Medicao = put("/api/medicoes/$id", form)
    override suspend fun excluirMedicao(id: Long) = delete("/api/medicoes/$id")

    override suspend fun listarTarifas(): List<Tarifa> = get("/api/tarifas")
    override suspend fun criarTarifa(form: TarifaForm): Tarifa = post("/api/tarifas", form)
    override suspend fun atualizarTarifa(id: Long, form: TarifaForm): Tarifa = put("/api/tarifas/$id", form)
    override suspend fun excluirTarifa(id: Long) = delete("/api/tarifas/$id")

    override suspend fun listarAlertas(): List<Alerta> = get("/api/alertas")
    override suspend fun criarAlerta(form: AlertaForm): Alerta = post("/api/alertas", form)
    override suspend fun atualizarAlerta(id: Long, form: AlertaForm): Alerta = put("/api/alertas/$id", form)
    override suspend fun excluirAlerta(id: Long) = delete("/api/alertas/$id")

    override suspend fun listarMetas(): List<MetaConsumo> = get("/api/metas-consumo")
    override suspend fun criarMeta(form: MetaConsumoForm): MetaConsumo = post("/api/metas-consumo", form)
    override suspend fun atualizarMeta(id: Long, form: MetaConsumoForm): MetaConsumo = put("/api/metas-consumo/$id", form)
    override suspend fun excluirMeta(id: Long) = delete("/api/metas-consumo/$id")

    override suspend fun listarRelatorios(): List<Relatorio> = get("/api/relatorios")
    override suspend fun criarRelatorio(form: RelatorioForm): Relatorio = post("/api/relatorios", form)
    override suspend fun atualizarRelatorio(id: Long, form: RelatorioForm): Relatorio = put("/api/relatorios/$id", form)
    override suspend fun excluirRelatorio(id: Long) = delete("/api/relatorios/$id")
    override suspend fun gerarRelatorio(form: GerarRelatorioForm): Relatorio = post("/api/relatorios/gerar", form)

    private suspend inline fun <reified T> get(caminho: String): T = tratar { http.get("$base$caminho").body() }

    private suspend inline fun <reified B, reified T> post(caminho: String, corpo: B): T = tratar {
        http.post("$base$caminho") { setBody(corpo) }.body()
    }

    private suspend inline fun <reified B, reified T> put(caminho: String, corpo: B): T = tratar {
        http.put("$base$caminho") { setBody(corpo) }.body()
    }

    private suspend fun delete(caminho: String) {
        tratar { http.delete("$base$caminho") }
    }

    private suspend fun <T> tratar(bloco: suspend () -> T): T = try {
        bloco()
    } catch (erro: ResponseException) {
        if (erro.response.status == HttpStatusCode.Unauthorized && sessao.token.value != null) {
            sessao.encerrar()
            throw ErroAmpera("Sessão expirada")
        }
        val mensagem = runCatching { json.decodeFromString<ErroApi>(erro.response.bodyAsText()).mensagem }.getOrNull()
        throw ErroAmpera(mensagem ?: "Falha na API (${erro.response.status.value})")
    }
}
