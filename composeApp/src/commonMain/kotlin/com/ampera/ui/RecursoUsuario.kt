package com.ampera.ui

import com.ampera.dados.AmperaRepositorio
import com.ampera.dados.UsuarioForm

internal fun recursoUsuario(repo: AmperaRepositorio) = recurso(
    titulo = "Usuários",
    singular = "Usuário",
    formulario = { c, e, m -> FormularioUsuario(c, e, m) },
    campos = {
        listOf(
            Campo("nome", "Nome"),
            Campo("email", "E-mail"),
            Campo("senha", "Senha (mín. 6, vazio mantém a atual)", TipoCampo.SENHA),
            Campo("telefone", "Telefone"),
            Campo("dataNascimento", "Data de nascimento (opcional)", TipoCampo.DATA),
            Campo("ativo", "Usuário ativo", TipoCampo.BOOLEANO),
        )
    },
    iniciais = { mapOf("ativo" to "true") },
    listar = {
        repo.listarUsuarios().map {
            ItemLista(
                it.id,
                it.nome,
                listOf(it.email, it.telefone).filter { texto -> texto.isNotBlank() }.joinToString(" · "),
                mapOf(
                    "nome" to it.nome,
                    "email" to it.email,
                    "telefone" to it.telefone,
                    "dataNascimento" to it.dataNascimento.orEmpty(),
                    "ativo" to it.ativo.toString(),
                ),
                etiqueta = if (it.ativo) null else "inativo",
            )
        }
    },
    salvar = { id, valores ->
        val form = UsuarioForm(
            valores.texto("nome", "Nome"),
            valores.texto("email", "E-mail"),
            valores["senha"]?.ifBlank { null },
            valores["telefone"]?.trim().orEmpty(),
            valores.dataOpcional("dataNascimento"),
            valores.booleano("ativo"),
        )
        if (id == null) repo.criarUsuario(form) else repo.atualizarUsuario(id, form)
    },
    excluir = { repo.excluirUsuario(it) },
)
