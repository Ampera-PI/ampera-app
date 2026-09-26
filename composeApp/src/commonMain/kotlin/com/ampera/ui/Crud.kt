package com.ampera.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ampera.dados.AmperaRepositorio
import com.ampera.dados.ErroAmpera
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TipoCampo { TEXTO, SENHA, NUMERO, DATA, BOOLEANO, SELECAO }

data class Campo(
    val chave: String,
    val rotulo: String,
    val tipo: TipoCampo = TipoCampo.TEXTO,
    val opcoes: List<Pair<String, String>> = emptyList(),
)

data class ItemLista(
    val id: Long,
    val titulo: String,
    val detalhe: String,
    val valores: Map<String, String>,
    val etiqueta: String? = null,
    val destaque: Boolean = false,
)

data class Catalogo(
    val residencias: List<Pair<String, String>> = emptyList(),
    val comodos: List<Pair<String, String>> = emptyList(),
    val dispositivos: List<Pair<String, String>> = emptyList(),
) {
    fun residencia(id: Long) = residencias.nome(id, "Residência")
    fun comodo(id: Long) = comodos.nome(id, "Cômodo")
    fun dispositivo(id: Long) = dispositivos.nome(id, "Dispositivo")

    private fun List<Pair<String, String>>.nome(id: Long, padrao: String) =
        firstOrNull { it.first == id.toString() }?.second ?: "$padrao $id"
}

interface ClienteRecurso {
    val titulo: String
    val singular: String
    val acaoExtra: String?
    fun campos(catalogo: Catalogo): List<Campo>

    @Composable
    fun Formulario(estado: CrudEstado, modelo: CrudViewModel)
    fun iniciais(catalogo: Catalogo): Map<String, String> = emptyMap()
    suspend fun listar(catalogo: Catalogo): List<ItemLista>
    suspend fun salvar(id: Long?, valores: Map<String, String>)
    suspend fun excluir(id: Long)
    suspend fun extra(valores: Map<String, String>) {}
}

data class CrudEstado(
    val recurso: String = "",
    val itens: List<ItemLista> = emptyList(),
    val catalogo: Catalogo = Catalogo(),
    val carregando: Boolean = true,
    val formulario: Boolean = false,
    val salvando: Boolean = false,
    val editandoId: Long? = null,
    val valores: Map<String, String> = emptyMap(),
    val confirmarExclusao: ItemLista? = null,
    val erro: String? = null,
    val aviso: String? = null,
)

sealed interface CrudIntent {
    data class Abrir(val cliente: ClienteRecurso) : CrudIntent
    data object Novo : CrudIntent
    data class Editar(val item: ItemLista) : CrudIntent
    data class Alterar(val chave: String, val valor: String) : CrudIntent
    data object Salvar : CrudIntent
    data object Limpar : CrudIntent
    data object Extra : CrudIntent
    data class PedirExclusao(val item: ItemLista) : CrudIntent
    data object ConfirmarExclusao : CrudIntent
    data object Fechar : CrudIntent
}

class CrudViewModel(private val repositorio: AmperaRepositorio) : ViewModel() {
    private val _estado = MutableStateFlow(CrudEstado())
    val estado: StateFlow<CrudEstado> = _estado
    private var cliente: ClienteRecurso? = null

    fun aoIntent(intent: CrudIntent) {
        when (intent) {
            is CrudIntent.Abrir -> {
                cliente = intent.cliente
                _estado.value = CrudEstado(recurso = intent.cliente.titulo)
                recarregar()
            }
            CrudIntent.Novo -> _estado.update {
                it.copy(formulario = true, editandoId = null, valores = cliente?.iniciais(it.catalogo).orEmpty(), erro = null, aviso = null)
            }
            is CrudIntent.Editar -> _estado.update {
                it.copy(formulario = true, editandoId = intent.item.id, valores = intent.item.valores, erro = null, aviso = null)
            }
            is CrudIntent.Alterar -> _estado.update { it.copy(valores = it.valores + (intent.chave to intent.valor)) }
            CrudIntent.Salvar -> executar("Registro salvo") { atual, estado -> atual.salvar(estado.editandoId, estado.valores) }
            CrudIntent.Limpar -> _estado.update { it.copy(valores = camposLimpos(it), erro = null, aviso = null) }
            CrudIntent.Extra -> executar("Relatório gerado com previsão") { atual, estado -> atual.extra(estado.valores) }
            is CrudIntent.PedirExclusao -> _estado.update { it.copy(confirmarExclusao = intent.item) }
            CrudIntent.ConfirmarExclusao -> excluir()
            CrudIntent.Fechar -> _estado.update { it.copy(formulario = false, confirmarExclusao = null, erro = null) }
        }
    }

    private fun camposLimpos(estado: CrudEstado): Map<String, String> {
        val atual = cliente ?: return emptyMap()
        val iniciais = atual.iniciais(estado.catalogo)
        return atual.campos(estado.catalogo).associate { campo ->
            campo.chave to when (campo.tipo) {
                TipoCampo.SELECAO -> iniciais[campo.chave].orEmpty()
                TipoCampo.BOOLEANO -> iniciais[campo.chave] ?: "true"
                else -> ""
            }
        }
    }

    private fun recarregar() {
        val atual = cliente ?: return
        viewModelScope.launch {
            try {
                val catalogo = Catalogo(
                    residencias = repositorio.listarResidencias().map { it.id.toString() to it.nome },
                    comodos = repositorio.listarComodos().map { it.id.toString() to it.nome },
                    dispositivos = repositorio.listarDispositivos().map { it.id.toString() to it.nome },
                )
                val itens = atual.listar(catalogo)
                if (cliente === atual) {
                    _estado.update { it.copy(itens = itens, catalogo = catalogo, carregando = false) }
                }
            } catch (erro: ErroAmpera) {
                _estado.update { it.copy(carregando = false, erro = erro.message) }
            } catch (_: Exception) {
                _estado.update { it.copy(carregando = false, erro = "Sem conexão com a API") }
            }
        }
    }

    private fun executar(aviso: String, acao: suspend (ClienteRecurso, CrudEstado) -> Unit) {
        val atual = cliente ?: return
        val estado = _estado.value
        if (estado.salvando) return
        viewModelScope.launch {
            _estado.update { it.copy(salvando = true, erro = null) }
            try {
                acao(atual, estado)
                _estado.update { it.copy(formulario = false, salvando = false, aviso = aviso) }
                recarregar()
            } catch (erro: ErroAmpera) {
                _estado.update { it.copy(salvando = false, erro = erro.message) }
            } catch (erro: Exception) {
                _estado.update { it.copy(salvando = false, erro = erro.message ?: "Não foi possível salvar") }
            }
        }
    }

    private fun excluir() {
        val atual = cliente ?: return
        val item = _estado.value.confirmarExclusao ?: return
        viewModelScope.launch {
            try {
                atual.excluir(item.id)
                _estado.update { it.copy(confirmarExclusao = null, aviso = "Registro excluído") }
                recarregar()
            } catch (erro: ErroAmpera) {
                _estado.update { it.copy(confirmarExclusao = null, erro = erro.message) }
            }
        }
    }
}

@Composable
fun CrudTela(modelo: CrudViewModel, cliente: ClienteRecurso, largo: Boolean) {
    val estado by modelo.estado.collectAsState()
    LaunchedEffect(cliente.titulo) {
        modelo.aoIntent(CrudIntent.Abrir(cliente))
    }
    Row(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f).fillMaxHeight().padding(horizontal = if (largo) 28.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(cliente.titulo, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        if (estado.carregando) "Carregando…" else "${estado.itens.size} registro(s)",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Button(
                    onClick = { modelo.aoIntent(CrudIntent.Novo) },
                    colors = ButtonDefaults.buttonColors(containerColor = Verde, contentColor = VerdeEscuro),
                    shape = FormaCampo,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Novo")
                }
            }
            if (!estado.formulario) {
                estado.erro?.let { Text(it, color = Perigo) }
            }
            estado.aviso?.let { Text(it, color = Verde, style = MaterialTheme.typography.labelLarge) }
            when {
                estado.carregando -> Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Verde)
                }
                estado.itens.isEmpty() -> Column(
                    Modifier.fillMaxWidth().cartao().padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text("Nada por aqui ainda", style = MaterialTheme.typography.titleMedium)
                    Text("Toque em Novo para cadastrar ${cliente.singular.lowercase()}.", color = TextoSuave)
                }
                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    items(estado.itens, key = { it.id }) { item ->
                        CartaoItem(
                            item,
                            selecionado = estado.formulario && estado.editandoId == item.id,
                            aoEditar = { modelo.aoIntent(CrudIntent.Editar(item)) },
                            aoExcluir = { modelo.aoIntent(CrudIntent.PedirExclusao(item)) },
                        )
                    }
                }
            }
        }
        if (largo) {
            AnimatedVisibility(
                estado.formulario,
                enter = slideInHorizontally { it } + fadeIn(),
                exit = slideOutHorizontally { it } + fadeOut(),
            ) {
                Column(
                    Modifier.width(400.dp).fillMaxHeight().background(Cartao).padding(24.dp),
                ) {
                    cliente.Formulario(estado, modelo)
                }
            }
        }
    }
    if (!largo && estado.formulario) {
        FolhaFormulario(cliente, estado, modelo)
    }
    estado.confirmarExclusao?.let { item ->
        AlertDialog(
            onDismissRequest = { modelo.aoIntent(CrudIntent.Fechar) },
            containerColor = CartaoAlto,
            icon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = Perigo) },
            title = { Text("Excluir ${cliente.singular.lowercase()}?") },
            text = { Text("“${item.titulo}” será removido definitivamente.", color = TextoSuave) },
            confirmButton = {
                Button(
                    onClick = { modelo.aoIntent(CrudIntent.ConfirmarExclusao) },
                    colors = ButtonDefaults.buttonColors(containerColor = Perigo, contentColor = Texto),
                ) { Text("Excluir") }
            },
            dismissButton = { TextButton(onClick = { modelo.aoIntent(CrudIntent.Fechar) }) { Text("Cancelar", color = TextoSuave) } },
        )
    }
}

@Composable
private fun CartaoItem(item: ItemLista, selecionado: Boolean, aoEditar: () -> Unit, aoExcluir: () -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .cartao(if (selecionado) CartaoAlto else Cartao)
            .clickable(onClick = aoEditar)
            .padding(start = 18.dp, top = 14.dp, bottom = 14.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.width(4.dp).height(40.dp).background(if (item.destaque) Perigo else Verde, RoundedCornerShape(2.dp)),
        )
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    item.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                item.etiqueta?.let {
                    Text(
                        it,
                        color = if (item.destaque) Perigo else Verde,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .background((if (item.destaque) Perigo else Verde).copy(alpha = 0.14f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
            }
            Text(item.detalhe, color = TextoSuave, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        IconButton(onClick = aoEditar) { Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = TextoSuave) }
        IconButton(onClick = aoExcluir) { Icon(Icons.Filled.Delete, contentDescription = "Excluir", tint = Perigo.copy(alpha = 0.8f)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FolhaFormulario(cliente: ClienteRecurso, estado: CrudEstado, modelo: CrudViewModel) {
    val folha = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = { modelo.aoIntent(CrudIntent.Fechar) },
        sheetState = folha,
        containerColor = Cartao,
    ) {
        Column(Modifier.padding(horizontal = 20.dp).padding(bottom = 28.dp)) {
            cliente.Formulario(estado, modelo)
        }
    }
}

@Composable
internal fun ColumnScope.CamposDo(cliente: ClienteRecurso, estado: CrudEstado, modelo: CrudViewModel, vararg chaves: String) {
    val campos = cliente.campos(estado.catalogo).associateBy { it.chave }
    chaves.mapNotNull { campos[it] }.forEach { campo ->
        CampoEditor(campo, estado.valores[campo.chave].orEmpty()) {
            modelo.aoIntent(CrudIntent.Alterar(campo.chave, it))
        }
    }
}

@Composable
internal fun FormularioBase(
    cliente: ClienteRecurso,
    estado: CrudEstado,
    modelo: CrudViewModel,
    campos: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(if (estado.editandoId == null) "NOVO" else "EDITAR", style = MaterialTheme.typography.labelMedium)
                Text(cliente.singular, style = MaterialTheme.typography.headlineSmall)
            }
            IconButton(onClick = { modelo.aoIntent(CrudIntent.Fechar) }) {
                Icon(Icons.Filled.Close, contentDescription = "Fechar", tint = TextoSuave)
            }
        }
        Column(
            Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            campos()
        }
        estado.erro?.let { Text(it, color = Perigo) }
        cliente.acaoExtra?.let { rotulo ->
            OutlinedButton(
                onClick = { modelo.aoIntent(CrudIntent.Extra) },
                enabled = !estado.salvando,
                modifier = Modifier.fillMaxWidth(),
                shape = FormaCampo,
            ) { Text(rotulo, color = Azul) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = { modelo.aoIntent(CrudIntent.Limpar) },
                enabled = !estado.salvando,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = FormaCampo,
            ) { Text("Limpar Campos", color = TextoSuave) }
            Button(
                onClick = { modelo.aoIntent(CrudIntent.Salvar) },
                enabled = !estado.salvando,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Verde, contentColor = VerdeEscuro),
                shape = FormaCampo,
            ) {
                if (estado.salvando) {
                    CircularProgressIndicator(Modifier.size(20.dp), color = VerdeEscuro, strokeWidth = 2.dp)
                } else {
                    Text("Gravar")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CampoEditor(campo: Campo, valor: String, onChange: (String) -> Unit) {
    when (campo.tipo) {
        TipoCampo.SELECAO -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(campo.rotulo, style = MaterialTheme.typography.labelMedium)
            if (campo.opcoes.isEmpty()) {
                Text("Nenhuma opção cadastrada", color = Aviso)
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                campo.opcoes.forEach { (id, nome) ->
                    FilterChip(
                        selected = valor == id,
                        onClick = { onChange(id) },
                        label = { Text(nome) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Verde.copy(alpha = 0.18f),
                            selectedLabelColor = Verde,
                            labelColor = Texto,
                        ),
                    )
                }
            }
        }
        TipoCampo.BOOLEANO -> Row(
            Modifier.fillMaxWidth().background(Fundo, FormaCampo).clickable { onChange(if (valor == "true") "false" else "true") }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(campo.rotulo, modifier = Modifier.weight(1f))
            Switch(
                checked = valor == "true",
                onCheckedChange = { onChange(if (it) "true" else "false") },
                colors = SwitchDefaults.colors(checkedTrackColor = Verde, checkedThumbColor = VerdeEscuro),
            )
        }
        else -> OutlinedTextField(
            value = valor,
            onValueChange = onChange,
            label = { Text(campo.rotulo) },
            placeholder = if (campo.tipo == TipoCampo.DATA) ({ Text("aaaa-mm-dd") }) else null,
            singleLine = true,
            visualTransformation = if (campo.tipo == TipoCampo.SENHA) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = when (campo.tipo) {
                    TipoCampo.NUMERO -> KeyboardType.Decimal
                    TipoCampo.SENHA -> KeyboardType.Password
                    else -> KeyboardType.Text
                },
            ),
            colors = coresDeCampo(),
            shape = FormaCampo,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun SeletorRecurso(opcoes: List<ClienteRecurso>, selecionado: String, onSelect: (ClienteRecurso) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        opcoes.forEach { opcao ->
            val ativo = opcao.titulo == selecionado
            Text(
                opcao.titulo,
                color = if (ativo) VerdeEscuro else Texto,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .background(if (ativo) Verde else CartaoAlto, RoundedCornerShape(50))
                    .clickable { onSelect(opcao) }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
            )
        }
    }
}
