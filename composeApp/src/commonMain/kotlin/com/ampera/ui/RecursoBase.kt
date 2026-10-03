package com.ampera.ui

import com.ampera.dados.ErroAmpera
import androidx.compose.runtime.Composable
import kotlinx.datetime.Clock
import kotlin.math.pow
import kotlin.math.round

internal fun recurso(
    titulo: String,
    singular: String,
    formulario: @Composable (ClienteRecurso, CrudEstado, CrudViewModel) -> Unit,
    acaoExtra: String? = null,
    campos: (Catalogo) -> List<Campo>,
    iniciais: (Catalogo) -> Map<String, String> = { emptyMap() },
    listar: suspend (Catalogo) -> List<ItemLista>,
    salvar: suspend (Long?, Map<String, String>) -> Unit,
    excluir: suspend (Long) -> Unit,
    extra: suspend (Map<String, String>) -> Unit = {},
): ClienteRecurso {
    val definirCampos = campos
    val definirIniciais = iniciais
    val carregar = listar
    val gravar = salvar
    val remover = excluir
    val executarExtra = extra
    val desenharFormulario = formulario
    return object : ClienteRecurso {
        override val titulo = titulo
        override val singular = singular
        override val acaoExtra = acaoExtra
        override fun campos(catalogo: Catalogo) = definirCampos(catalogo)
        override fun iniciais(catalogo: Catalogo) = definirIniciais(catalogo)

        @Composable
        override fun Formulario(estado: CrudEstado, modelo: CrudViewModel) = desenharFormulario(this, estado, modelo)

        override suspend fun listar(catalogo: Catalogo) = carregar(catalogo)
        override suspend fun salvar(id: Long?, valores: Map<String, String>) = gravar(id, valores)
        override suspend fun excluir(id: Long) = remover(id)
        override suspend fun extra(valores: Map<String, String>) = executarExtra(valores)
    }
}

internal fun primeiro(chave: String, opcoes: List<Pair<String, String>>): Map<String, String> =
    opcoes.firstOrNull()?.let { mapOf(chave to it.first) }.orEmpty()

internal fun hoje(): String = Clock.System.now().toString().take(10)

internal fun inicioDoMes(): String = hoje().take(8) + "01"

internal fun dataHora(iso: String): String = iso.take(19).replace("T", " ")

internal fun Map<String, String>.texto(chave: String, rotulo: String): String =
    this[chave]?.trim().orEmpty().ifBlank { throw ErroAmpera("Preencha $rotulo") }

internal fun Map<String, String>.longo(chave: String, rotulo: String): Long =
    this[chave]?.toLongOrNull() ?: throw ErroAmpera("Selecione $rotulo")

internal fun Map<String, String>.numero(chave: String): Double {
    val texto = this[chave]?.trim().orEmpty()
    if (texto.isBlank()) return 0.0
    return texto.replace(",", ".").toDoubleOrNull() ?: throw ErroAmpera("Valor inválido: $texto")
}

internal fun Map<String, String>.inteiro(chave: String, padrao: Int): Int {
    val texto = this[chave]?.trim().orEmpty()
    if (texto.isBlank()) return padrao
    return texto.toIntOrNull() ?: throw ErroAmpera("Informe um número inteiro: $texto")
}

internal fun Map<String, String>.dataOpcional(chave: String): String? = this[chave]?.trim()?.ifBlank { null }

internal fun Map<String, String>.booleano(chave: String): Boolean = this[chave] != "false"

fun numero(valor: Double, casas: Int): String {
    val fator = 10.0.pow(casas)
    val arredondado = round(valor * fator) / fator
    val negativo = arredondado < 0
    val inteiro = kotlin.math.abs(arredondado).toLong()
    val milhar = inteiro.toString().reversed().chunked(3).joinToString(".").reversed()
    val sinal = if (negativo) "-" else ""
    if (casas == 0) return sinal + milhar
    val decimal = round((kotlin.math.abs(arredondado) - inteiro) * fator).toLong().toString().padStart(casas, '0')
    return "$sinal$milhar,$decimal"
}
