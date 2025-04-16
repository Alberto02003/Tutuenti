package com.example.tutuenti.pantallas

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

import com.example.tutuenti.bd.Mensaje
import com.example.tutuenti.metodos.Cabecera
import com.example.tutuenti.metodos.Escribir
import com.example.tutuenti.metodos.MensajeItem
import com.example.tutuenti.metodos.Menu
import com.example.tutuenti.metodos.SearchBar
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

import androidx.activity.compose.BackHandler
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text

@Composable
fun TuTuentiApp(navController: NavController) {
    var isMenuVisible by remember { mutableStateOf(false) }
    var buscadorVisible by remember { mutableStateOf(false) }
    var mostrandoMensajes by remember { mutableStateOf(false) }
    val db = Firebase.firestore
    val context = LocalContext.current

    var mensajes by remember { mutableStateOf<List<Mensaje>>(emptyList()) }
    var puedeSalir by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db.collection("mensajes")
            .orderBy("fechaHora", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                val mensajeList = snapshot?.documents?.map { doc ->
                    doc.toObject(Mensaje::class.java) ?: Mensaje()
                } ?: emptyList()

                mensajes = mensajeList
                puedeSalir = mensajes.isNotEmpty()
            }
    }

    BackHandler(enabled = !puedeSalir) {
        Toast.makeText(context, "No puedes salir mientras no haya mensajes.", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Cabecera(
            isMenuVisible = isMenuVisible,
            onMenuToggle = {
                buscadorVisible = false
                isMenuVisible = !isMenuVisible

            },
            navController = navController
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (mensajes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay mensajes disponibles.",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(mensajes) { mensaje ->
                       MensajeItem(mensaje)
                    }
                }
            }

            if (mostrandoMensajes) {
                Escribir(
                    onCerrarClick = {
                        mostrandoMensajes = false
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            } else {
                FloatingActionButton(
                    onClick = {
                        mostrandoMensajes = true
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir",
                        tint = Color.White
                    )
                }
            }

            when {
                buscadorVisible -> {
                    SearchBar(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth(),
                        onCloseSearch = {
                            buscadorVisible = false
                            isMenuVisible = true
                        },
                        navController = navController
                    )
                }
                isMenuVisible -> {
                    Menu(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(8.dp),
                        navController = navController,
                        onSearchClicked = {
                            buscadorVisible = true
                            isMenuVisible = false
                        }
                    )
                }
            }
        }
    }
}





