package com.example.tutuenti.rutas


sealed class Rutas(val ruta: String) {

    object InicioSesion : Rutas("InicioSesion")

    object Registro : Rutas("Registro")

    object Perfil : Rutas("Perfil")

    object TuTuenti : Rutas("TuTuenti")

    object Configuracion : Rutas("Configuracion")

    object Favoritos : Rutas("Favoritos")

}
