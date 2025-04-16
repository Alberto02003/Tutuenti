package com.example.tutuenti.pantallas
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tutuenti.R
import com.example.tutuenti.bd.Mensaje
import com.example.tutuenti.metodos.Cabecera
import com.example.tutuenti.metodos.Menu
import com.example.tutuenti.metodos.SearchBar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

@Composable
fun Perfil(navController: NavController, nombreUsuario: String) {
    var menuVisible by remember { mutableStateOf(false) }
    var buscadorVisible by remember { mutableStateOf(false) }
    var usuarioNombre by remember { mutableStateOf("Cargando...") }
    var seguidoresCantidad by remember { mutableStateOf(0) }
    var mensajes by remember { mutableStateOf<List<Mensaje>>(emptyList()) }
    var imagenPerfilUrl by remember { mutableStateOf("") }
    var selectedText by remember { mutableStateOf(0) }

    val context = LocalContext.current

    LaunchedEffect(nombreUsuario) {
        val db = Firebase.firestore

        db.collection("usuarios")
            .whereEqualTo("nombre", nombreUsuario)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                snapshot?.documents?.firstOrNull()?.let { userDoc ->
                    usuarioNombre = userDoc.getString("nombre") ?: "Usuario Desconocido"
                    seguidoresCantidad = userDoc.getLong("cantidadSeguidores")?.toInt() ?: 0
                    imagenPerfilUrl = userDoc.getString("imagenPerfilUsuario") ?: ""
                }
            }

        db.collection("mensajes")
            .whereEqualTo("remitenteId", nombreUsuario)
            .orderBy("fechaHora", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                mensajes = snapshot?.documents?.map { doc ->
                    doc.toObject(Mensaje::class.java) ?: Mensaje()
                } ?: emptyList()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Cabecera(
            isMenuVisible = menuVisible,
            onMenuToggle = {
                buscadorVisible = false
                menuVisible = !menuVisible

            }, navController = navController
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(16.dp)
                        ) {
                            AsyncImage(
                                model = imagenPerfilUrl,
                                contentDescription = "Foto de perfil",
                                placeholder = painterResource(R.drawable.sync_problem_24dp_000000_fill0_wght400_grad0_opsz24),
                                error = painterResource(R.drawable.person_40dp_000000_fill0_wght400_grad0_opsz40),
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.background)
                                    .align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = usuarioNombre,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Seguidores",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = seguidoresCantidad.toString(),
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Mensajes",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = mensajes.size.toString(),
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 12.dp, bottom = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Mensajes
                                    Text(
                                        text = "Mensajes",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textDecoration = if (selectedText == 1) TextDecoration.Underline else TextDecoration.None
                                        ),
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .clickable { selectedText = 1 }
                                    )

                                    Text(
                                        text = "Likes",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textDecoration = if (selectedText == 2) TextDecoration.Underline else TextDecoration.None
                                        ),
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .clickable { selectedText = 2 }
                                    )


                                    Text(
                                        text = "Comentarios",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textDecoration = if (selectedText == 3) TextDecoration.Underline else TextDecoration.None
                                        ),
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .clickable { selectedText = 3 }
                                    )
                                }
                            }
                        }
                    }
                    when (selectedText) {
                        1 -> {
                            if (mensajes.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "No hay mensajes disponibles.",
                                            color = Color.Gray
                                        )
                                    }
                                }
                            } else {
                                items(mensajes) { mensaje ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                            .background(
                                                MaterialTheme.colorScheme.surface,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = mensaje.contenido,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Publicado: ${mensaje.fechaHora}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }

                        2 -> {

                        }

                        3 -> {

                        }
                    }
                }

            }
            when {
                buscadorVisible -> {
                    SearchBar(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth(),
                        onCloseSearch = {
                            buscadorVisible = false
                            menuVisible = true
                        },
                        navController = navController
                    )
                }

                menuVisible -> {
                    Menu(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(8.dp),
                        navController = navController,
                        onSearchClicked = {
                            buscadorVisible = true
                            menuVisible = false
                        }
                    )
                }
            }
        }
    }
}
