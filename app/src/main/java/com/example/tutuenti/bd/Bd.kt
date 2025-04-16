package com.example.tutuenti.bd

import com.example.tutuenti.R

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val cantidadSeguidores: Int = 0,
    val imagenPerfilUsuario: String = ""
)

data class Seguidores(
    val usuarioId: String = "",
    val listaSeguidores: List<String> = emptyList()
)

data class Mensaje(
    val mensajeId: String = "",
    val contenido: String = "",
    val remitenteId: String = "",
    val fechaHora: String = "",
)

data class Comentario(
    val comentarioId: String = "",
    val mensaje: String  = "",
    val usuarioId: String = "",
    val imagenPerfil: String,
    val fechaHora: String = ""
)




