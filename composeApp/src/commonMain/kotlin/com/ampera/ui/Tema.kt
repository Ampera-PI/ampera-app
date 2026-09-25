package com.ampera.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Fundo = Color(0xFF0E1116)
val Cartao = Color(0xFF171C24)
val CartaoAlto = Color(0xFF1E2633)
val Verde = Color(0xFF23C48E)
val VerdeEscuro = Color(0xFF04281C)
val Texto = Color(0xFFF4F7FB)
val TextoSuave = Color(0xFF93A0B5)
val Perigo = Color(0xFFFF5A5A)
val Aviso = Color(0xFFFFB020)
val Linha = Color(0xFF2C3544)
val Azul = Color(0xFF4CC9F0)
val Roxo = Color(0xFFA78BFA)

private val Cores = darkColorScheme(
    primary = Verde,
    onPrimary = VerdeEscuro,
    secondary = Azul,
    background = Fundo,
    surface = Cartao,
    surfaceVariant = CartaoAlto,
    surfaceContainer = Cartao,
    surfaceContainerHigh = CartaoAlto,
    surfaceContainerLow = Cartao,
    secondaryContainer = VerdeEscuro,
    onSecondaryContainer = Verde,
    onBackground = Texto,
    onSurface = Texto,
    onSurfaceVariant = TextoSuave,
    error = Perigo,
    outline = Linha,
    outlineVariant = Linha,
)

private val Tipos = Typography(
    displaySmall = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Texto, letterSpacing = (-1).sp),
    headlineLarge = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.SemiBold, color = Texto, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.SemiBold, color = Texto),
    headlineSmall = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = Texto),
    titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Texto),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Texto),
    bodyLarge = TextStyle(fontSize = 15.sp, color = Texto),
    bodyMedium = TextStyle(fontSize = 14.sp, color = Texto),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    labelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextoSuave, letterSpacing = 0.6.sp),
)

val FormaCartao = RoundedCornerShape(20.dp)
val FormaCampo = RoundedCornerShape(14.dp)

@Composable
fun AmperaTheme(conteudo: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Cores, typography = Tipos, content = conteudo)
}

fun Modifier.cartao(cor: Color = Cartao): Modifier =
    background(cor, FormaCartao).border(1.dp, Linha.copy(alpha = 0.6f), FormaCartao)

@Composable
fun coresDeCampo() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Verde,
    unfocusedBorderColor = Linha,
    focusedTextColor = Texto,
    unfocusedTextColor = Texto,
    focusedLabelColor = Verde,
    unfocusedLabelColor = TextoSuave,
    cursorColor = Verde,
    focusedContainerColor = Fundo,
    unfocusedContainerColor = Fundo,
)

val Raio: ImageVector = ImageVector.Builder("Raio", 24.dp, 24.dp, 24f, 24f).apply {
    path(fill = SolidColor(Color.Black)) {
        moveTo(13.5f, 2f)
        lineTo(4.5f, 13.5f)
        horizontalLineTo(11f)
        lineTo(10f, 22f)
        lineTo(19.5f, 10f)
        horizontalLineTo(13f)
        close()
    }
}.build()

@Composable
fun SeloIcone(icone: ImageVector, cor: Color, tamanho: Dp = 36.dp) {
    Box(
        Modifier.size(tamanho).background(cor.copy(alpha = 0.14f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icone, contentDescription = null, tint = cor, modifier = Modifier.size(tamanho * 0.55f))
    }
}
