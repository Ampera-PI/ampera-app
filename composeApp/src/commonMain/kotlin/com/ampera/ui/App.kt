package com.ampera.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ampera.dados.AmperaApi
import com.ampera.dados.AmperaRepositorio
import com.ampera.dados.AutenticacaoRepositorio
import com.ampera.dados.ErroAmpera
import com.ampera.dados.Sessao
import com.ampera.dados.UsuarioForm
import com.ampera.dados.urlDaApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private enum class Aba(val rotulo: String, val icone: ImageVector) {
    CASA("Casa", Icons.Filled.LocationOn),
    ENERGIA("Energia", Raio),
    CONTA("Conta", Icons.Filled.AccountCircle),
}

data class LoginEstado(
    val email: String = "demo@ampera.local",
    val senha: String = "demo123",
    val nome: String = "",
    val cadastro: Boolean = false,
    val erro: String? = null,
    val ocupado: Boolean = false,
)

sealed interface LoginIntent {
    data class Email(val valor: String) : LoginIntent
    data class Senha(val valor: String) : LoginIntent
    data class Nome(val valor: String) : LoginIntent
    data object Alternar : LoginIntent
    data object Enviar : LoginIntent
}

class LoginViewModel(
    private val autenticacao: AutenticacaoRepositorio,
    private val sessao: Sessao,
) : ViewModel() {
    private val _estado = MutableStateFlow(LoginEstado())
    val estado: StateFlow<LoginEstado> = _estado

    fun aoIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Email -> _estado.update { it.copy(email = intent.valor) }
            is LoginIntent.Senha -> _estado.update { it.copy(senha = intent.valor) }
            is LoginIntent.Nome -> _estado.update { it.copy(nome = intent.valor) }
            LoginIntent.Alternar -> _estado.update { it.copy(cadastro = !it.cadastro, erro = null) }
            LoginIntent.Enviar -> enviar()
        }
    }

    private fun enviar() {
        val atual = _estado.value
        if (atual.ocupado) return
        viewModelScope.launch {
            _estado.update { it.copy(ocupado = true, erro = null) }
            try {
                if (atual.cadastro) {
                    autenticacao.registrar(UsuarioForm(atual.nome.ifBlank { "Novo usuário" }, atual.email.trim(), atual.senha))
                }
                sessao.iniciar(autenticacao.login(atual.email.trim(), atual.senha).token)
            } catch (erro: ErroAmpera) {
                _estado.update { it.copy(ocupado = false, erro = erro.message) }
            } catch (_: Exception) {
                _estado.update { it.copy(ocupado = false, erro = "Não foi possível falar com a API em ${urlDaApi()}") }
            }
        }
    }
}

@Composable
fun App() {
    AmperaTheme {
        val sessao = remember { Sessao() }
        val api = remember { AmperaApi(urlDaApi(), sessao) }
        val token by sessao.token.collectAsState()
        Box(Modifier.fillMaxSize().background(Fundo).safeDrawingPadding()) {
            if (token == null) {
                val login = remember { LoginViewModel(api, sessao) }
                LoginTela(login)
            } else {
                Shell(api, aoSair = sessao::encerrar)
            }
        }
    }
}

@Composable
private fun LoginTela(modelo: LoginViewModel) {
    val estado by modelo.estado.collectAsState()
    Box(
        Modifier.fillMaxSize().background(
            Brush.radialGradient(listOf(Verde.copy(alpha = 0.16f), Fundo), radius = 900f),
        ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.widthIn(max = 400.dp).fillMaxWidth().padding(20.dp).verticalScroll(rememberScrollState())
                .cartao().padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(48.dp).background(Verde, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                    Icon(Raio, contentDescription = null, tint = VerdeEscuro, modifier = Modifier.size(28.dp))
                }
                Column {
                    Text("Ampéra", style = MaterialTheme.typography.headlineLarge)
                    Text("Monitoramento de energia", color = TextoSuave)
                }
            }
            Spacer(Modifier.height(4.dp))
            AnimatedVisibility(estado.cadastro) {
                CampoLogin("Nome", estado.nome) { modelo.aoIntent(LoginIntent.Nome(it)) }
            }
            CampoLogin("E-mail", estado.email) { modelo.aoIntent(LoginIntent.Email(it)) }
            CampoLogin("Senha", estado.senha, senha = true) { modelo.aoIntent(LoginIntent.Senha(it)) }
            estado.erro?.let { Text(it, color = Perigo, style = MaterialTheme.typography.bodyMedium) }
            Button(
                onClick = { modelo.aoIntent(LoginIntent.Enviar) },
                enabled = !estado.ocupado,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Verde, contentColor = VerdeEscuro),
                shape = FormaCampo,
            ) {
                if (estado.ocupado) {
                    CircularProgressIndicator(Modifier.size(20.dp), color = VerdeEscuro, strokeWidth = 2.dp)
                } else {
                    Text(if (estado.cadastro) "Criar conta" else "Entrar", style = MaterialTheme.typography.labelLarge)
                }
            }
            TextButton(onClick = { modelo.aoIntent(LoginIntent.Alternar) }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(if (estado.cadastro) "Já tenho conta" else "Criar uma conta", color = TextoSuave)
            }
        }
    }
}

@Composable
private fun CampoLogin(rotulo: String, valor: String, senha: Boolean = false, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(rotulo) },
        singleLine = true,
        visualTransformation = if (senha) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = FormaCampo,
        colors = coresDeCampo(),
    )
}

@Composable
private fun Shell(api: AmperaRepositorio, aoSair: () -> Unit) {
    val crud = remember { CrudViewModel(api) }
    val secoes = remember {
        mapOf(
            Aba.CASA to recursosCasa(api),
            Aba.ENERGIA to recursosEnergia(api),
            Aba.CONTA to recursosConta(api),
        )
    }
    var aba by remember { mutableStateOf(Aba.CASA) }
    val escolhidos = remember { mutableStateOf(secoes.mapValues { it.value.first() }) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val largo = maxWidth >= 840.dp
        Row(Modifier.fillMaxSize()) {
            if (largo) Trilho(aba, aoSair) { aba = it }
            Column(Modifier.weight(1f).fillMaxHeight()) {
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    val opcoes = secoes.getValue(aba)
                    val atual = escolhidos.value.getValue(aba)
                    Column(Modifier.fillMaxSize()) {
                        if (!largo) TextButton(onClick = aoSair) { Text("Sair") }
                        SeletorRecurso(opcoes, atual.titulo) { escolhidos.value = escolhidos.value + (aba to it) }
                        CrudTela(crud, atual, largo)
                    }
                }
                if (!largo) Barra(aba) { aba = it }
            }
        }
    }
}

@Composable
private fun Trilho(aba: Aba, aoSair: () -> Unit, onAba: (Aba) -> Unit) {
    Column(
        Modifier.width(232.dp).fillMaxHeight().background(Cartao).padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            Modifier.padding(start = 8.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(Modifier.size(34.dp).background(Verde, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                Icon(Raio, contentDescription = null, tint = VerdeEscuro, modifier = Modifier.size(20.dp))
            }
            Text("Ampéra", style = MaterialTheme.typography.titleLarge)
        }
        Aba.entries.forEach { item ->
            val ativo = item == aba
            Row(
                Modifier.fillMaxWidth()
                    .background(if (ativo) Verde.copy(alpha = 0.14f) else Cartao, RoundedCornerShape(12.dp))
                    .clickable { onAba(item) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(item.icone, contentDescription = null, tint = if (ativo) Verde else TextoSuave, modifier = Modifier.size(20.dp))
                Text(item.rotulo, color = if (ativo) Verde else Texto, style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            Modifier.fillMaxWidth().clickable(onClick = aoSair).padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = TextoSuave, modifier = Modifier.size(20.dp))
            Text("Sair", color = TextoSuave, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun Barra(aba: Aba, onAba: (Aba) -> Unit) {
    NavigationBar(containerColor = Cartao, tonalElevation = 0.dp) {
        Aba.entries.forEach { item ->
            NavigationBarItem(
                selected = item == aba,
                onClick = { onAba(item) },
                icon = { Icon(item.icone, contentDescription = item.rotulo) },
                label = { Text(item.rotulo) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Verde,
                    selectedTextColor = Verde,
                    indicatorColor = Verde.copy(alpha = 0.14f),
                    unselectedIconColor = TextoSuave,
                    unselectedTextColor = TextoSuave,
                ),
            )
        }
    }
}