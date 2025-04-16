package com.example.tutuenti.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tutuenti.pantallas.Configuracion
import com.example.tutuenti.pantallas.Favoritos
import com.example.tutuenti.pantallas.InicioSesionScreen
import com.example.tutuenti.pantallas.Perfil
import com.example.tutuenti.pantallas.RegistroScreen
import com.example.tutuenti.pantallas.TuTuentiApp
import com.example.tutuenti.rutas.Rutas

@Composable
fun GrafoNav(isDarkModeEnabled: Boolean, function: (Boolean) -> Unit) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.InicioSesion.ruta
    )
    {
        composable(Rutas.InicioSesion.ruta) {
            InicioSesionScreen(navController)
        }
        composable(Rutas.Configuracion.ruta) {
            Configuracion(navController,function)
        }
        composable(Rutas.Favoritos.ruta) {
            Favoritos(navController)
        }
        composable(Rutas.Registro.ruta) {
            RegistroScreen(navController)
        }
        composable("Perfil/{usuarioNombre}") { backStackEntry ->
            val usuarioNombre = backStackEntry.arguments?.getString("usuarioNombre")
            Perfil(navController = navController, nombreUsuario = usuarioNombre ?: "Usuario no encontrado")
        }

        composable(Rutas.TuTuenti.ruta) {
            TuTuentiApp(navController)
        }
    }
}




