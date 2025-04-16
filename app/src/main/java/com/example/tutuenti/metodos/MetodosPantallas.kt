package com.example.tutuenti.metodos

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.tutuenti.R
import com.example.tutuenti.bd.Comentario
import com.example.tutuenti.bd.Mensaje
import com.example.tutuenti.bd.Usuario
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


@Composable
fun Cabecera(
    isMenuVisible: Boolean,
    onMenuToggle: () -> Unit,
    navController: NavController
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Image(
                painter = painterResource(id = R.drawable.tuenti),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(35.dp)
                    .clickable { navController.navigate("TuTuenti") }
            )


            Text(
                text = ".TuTuenti",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.padding(start = 16.dp)
            )


            IconButton(onClick = onMenuToggle) {
                Icon(
                    imageVector = if (isMenuVisible) Icons.Default.Close else Icons.Default.Menu,
                    contentDescription = if (isMenuVisible) "Cerrar Menú" else "Abrir Menú",
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }
    }
}



@Composable
fun Menu(
    modifier: Modifier = Modifier,
    navController: NavController,
    onSearchClicked: () -> Unit
) {

    val currentUser = Firebase.auth.currentUser
    val usuarioNombre = currentUser?.email?.substringBefore("@") ?: "Usuario no encontrado"
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = { navController.navigate("TuTuenti") }) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(onClick = onSearchClicked) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(onClick = { navController.navigate("Perfil/$usuarioNombre") }) {
                Icon(
                    imageVector = Icons.Default.Person,
                    tint = MaterialTheme.colorScheme.background,
                    contentDescription = "Profile",
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(onClick = { /* Acción al pulsar Notificaciones */ }) {
                Icon(
                    imageVector = Icons.Default.Star,
                    tint = MaterialTheme.colorScheme.background,
                    contentDescription = "Notifications",
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(onClick = { navController.navigate("Configuracion") }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    tint = MaterialTheme.colorScheme.background,
                    contentDescription = "Settings",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(modifier: Modifier = Modifier, onCloseSearch: () -> Unit, navController: NavController) {
    var query by remember { mutableStateOf("") }
    var usersWithImages by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) } // Almacena par de nombre y URL de imagen
    val db = Firebase.firestore


    LaunchedEffect(Unit) {
        db.collection("usuarios")
            .addSnapshotListener { result, exception ->
                if (exception != null) {
                    Log.e("SearchBar", "Error obteniendo usuarios: ", exception)
                    return@addSnapshotListener
                }
                if (result != null) {

                    usersWithImages = result.mapNotNull { doc ->
                        val nombre = doc.getString("nombre")
                        val imagenPerfil = doc.getString("imagenPerfilUsuario")
                        if (nombre != null && imagenPerfil != null) {
                            Pair(nombre, imagenPerfil)
                        } else {
                            null
                        }
                    }
                }
            }
    }
    val filteredUsers = usersWithImages.filter { it.first.contains(query, ignoreCase = true) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Buscar...") },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            IconButton(onClick = onCloseSearch) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar búsqueda",
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }

        if (query.isNotEmpty() && filteredUsers.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f))
            ) {
                items(filteredUsers) { user ->
                    TextButton(
                        onClick = {
                            navController.navigate("Perfil/${user.first}")
                        },
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                    ) {
                        AsyncImage(
                            model = user.second,
                            contentDescription = "Foto de perfil",
                            placeholder = painterResource(R.drawable.sync_problem_24dp_000000_fill0_wght400_grad0_opsz24),
                            error = painterResource(R.drawable.person_40dp_000000_fill0_wght400_grad0_opsz40),
                            modifier = Modifier
                                .size(25.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background)
                        )
                        Text(text = user.first, color = Color.Black)
                    }
                }
            }
        } else if (query.isNotEmpty()) {
            Text(
                text = "No se encontraron usuarios.",
                modifier = Modifier.padding(8.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)),
                color = Color.Gray
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Escribir(
    onCerrarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mensaje by remember { mutableStateOf("") }
    var nombreUsuario by remember { mutableStateOf("") }
    var imagenPerfil by remember { mutableStateOf("") }
    val context = LocalContext.current
    val auth = Firebase.auth

    val currentUser = auth.currentUser
    val remitenteEmail = currentUser?.email ?: ""

    LaunchedEffect(remitenteEmail) {
        if (remitenteEmail.isNotEmpty()) {
            val db = Firebase.firestore
            val userDoc = db.collection("usuarios")
                .whereEqualTo("email", remitenteEmail)
                .get()
                .await()
                .documents
                .firstOrNull()
            if (userDoc != null) {
                nombreUsuario = userDoc.getString("nombre") ?: "Usuario"
                imagenPerfil = userDoc.getString("imagenPerfilUsuario") ?: ""
            } else {
                nombreUsuario = "Usuario no encontrado"
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nuevo Mensaje",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(
                    onClick = { onCerrarClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = mensaje,
                    onValueChange = { mensaje = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text(text = "Escribe un mensaje...") },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    maxLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )
                FloatingActionButton(
                    onClick = {
                        if (mensaje.isNotBlank() && remitenteEmail.isNotBlank()) {
                            subirMensaje(
                                contenido = mensaje,
                                emailRemitente = remitenteEmail,
                                onSuccess = {
                                    Toast.makeText(context, "Mensaje enviado con éxito", Toast.LENGTH_SHORT).show()
                                    mensaje = ""
                                },
                                onFailure = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            Toast.makeText(context, "No se pudo obtener el remitente", Toast.LENGTH_SHORT).show()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar mensaje"
                    )
                }
            }
        }
    }
}

@Composable
fun MensajeItem(mensaje: Mensaje) {
    var imagenPerfilUrl by remember { mutableStateOf("") }
    var nombreUsuario by remember { mutableStateOf("") }
    var like by remember { mutableStateOf(false) }
    var mostrarComentarioPreview by remember { mutableStateOf(false) }
    val comentarios = remember { mutableStateListOf<Map<String, Any>>() }
    val db = Firebase.firestore

    LaunchedEffect(mensaje.remitenteId) {
        obtenerUsuario(mensaje.remitenteId, onSuccess = { usuario ->
            imagenPerfilUrl = usuario.imagenPerfilUsuario
            nombreUsuario = usuario.nombre
        }, onFailure = {
            imagenPerfilUrl = ""
            nombreUsuario = "Usuario Desconocido"
        })
    }

    LaunchedEffect(mensaje.mensajeId) {
        db.collection("comentarios")
            .whereEqualTo("mensajeId", mensaje.mensajeId)
            .orderBy("fechaHora", Query.Direction.DESCENDING)
            .limit(2)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MensajeItem", "Error al cargar comentarios", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    comentarios.clear()
                    comentarios.addAll(snapshot.documents.mapNotNull { it.data })
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imagenPerfilUrl,
                contentDescription = "Foto de perfil",
                placeholder = painterResource(R.drawable.sync_problem_24dp_000000_fill0_wght400_grad0_opsz24),
                error = painterResource(R.drawable.person_40dp_000000_fill0_wght400_grad0_opsz40),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = nombreUsuario,
                style = TextStyle(color = Color.Gray ,fontWeight = FontWeight.Bold, fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Fecha: ${mensaje.fechaHora}",
                style = TextStyle(color = Color.Gray, fontSize = 14.sp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.width(60.dp))
            Text(
                color = Color.Gray,
                text = mensaje.contenido,
                style = TextStyle(fontSize = 14.sp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (mostrarComentarioPreview) {
            if (comentarios.isEmpty()) {
                Text(
                    text = "Aún no hay comentarios. ¡Sé el primero en comentar!",
                    style = TextStyle(color = Color.Gray, fontSize = 14.sp),
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(comentarios) { comentario ->
                        ComentarioItem(comentario = comentario)
                    }
                }
            }
            ComentarioPreviewItem(
                mensajeId = mensaje.mensajeId,
                onComentarioAgregado = { nuevoComentario ->

                }
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                mostrarComentarioPreview = !mostrarComentarioPreview
            }) {
                Image(
                    painter = painterResource(
                        id = if (mostrarComentarioPreview)
                            R.drawable.comment_24dp_2854c5_fill0_wght400_grad0_opsz24
                        else R.drawable.comment_40dp_999999_fill0_wght400_grad0_opsz40
                    ),
                    contentDescription = "Botón de comentarios",
                    modifier = Modifier.size(35.dp)
                )
            }
            IconButton(onClick = { /* Acción al pulsar botón 2 */ }) {
                Image(
                    painter = painterResource(R.drawable.autorenew_40dp_999999_fill0_wght400_grad0_opsz40),
                    contentDescription = "Botón de acción 2",
                    modifier = Modifier.size(35.dp)
                )
            }
            IconButton(onClick = { like = !like }) {
                Image(
                    painter = painterResource(
                        id = if (like)
                            R.drawable.favorite_24dp_ea3323_fill0_wght400_grad0_opsz24
                        else R.drawable.favorite_40dp_999999_fill0_wght400_grad0_opsz40
                    ),
                    contentDescription = "Botón de me gusta",
                    modifier = Modifier.size(35.dp)
                )
            }
            IconButton(onClick = { /* Acción al pulsar botón 4 */ }) {
                Image(
                    painter = painterResource(R.drawable.share_40dp_999999_fill0_wght400_grad0_opsz40),
                    contentDescription = "Botón de compartir",
                    modifier = Modifier.size(35.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComentarioPreviewItem(
    mensajeId: String,
    onComentarioAgregado: (String) -> Unit
) {
    var comentarioTexto by remember { mutableStateOf("") }
    var mostrarFormulario by remember { mutableStateOf(false) }
    val db = Firebase.firestore
    val auth = Firebase.auth
    val usuarioActual = auth.currentUser
    val usuarioEmail = usuarioActual?.email.orEmpty()
    var nombreUsuario by remember { mutableStateOf("") }
    var imagenPerfilUrl by remember { mutableStateOf("") }
    val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        .format(System.currentTimeMillis()).toString()

    LaunchedEffect(usuarioActual?.uid) {
        if (usuarioActual != null) {
            var correo = usuarioActual.email ?: "Correo no disponible"

            val db = Firebase.firestore
            val userId = usuarioActual.uid

            db.collection("usuarios").document(userId)
                .addSnapshotListener { document, exception ->
                    if (exception != null) {
                        return@addSnapshotListener
                    }

                    if (document != null && document.exists()) {
                        nombreUsuario = document.getString("nombre") ?: "Nombre no disponible"
                        imagenPerfilUrl = document.getString("imagenPerfilUsuario") ?: ""
                    } else {

                    }
                }
        } else {

        }
    }


    if (mostrarFormulario) {
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nuevo Comentario",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(
                    onClick = { mostrarFormulario = false }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = comentarioTexto,
                    onValueChange = { comentarioTexto = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text(text = "Escribe un comentario...") },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    maxLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )
                FloatingActionButton(
                    onClick = {
                        if (comentarioTexto.isNotBlank()) {
                            val nuevoComentario = mapOf(
                                "mensajeId" to mensajeId,
                                "texto" to comentarioTexto,
                                "autor" to nombreUsuario.ifEmpty { "Usuario Anónimo" },
                                "autorEmail" to usuarioEmail,
                                "imagenPerfil" to imagenPerfilUrl,
                                "fechaHora" to fechaActual
                            )

                            db.collection("comentarios")
                                .add(nuevoComentario)
                                .addOnSuccessListener { comentarioRef ->
                                    val comentarioId = comentarioRef.id


                                    db.collection("mensajes").document(mensajeId)
                                        .update("comentarios", FieldValue.arrayUnion(comentarioId))
                                        .addOnSuccessListener {
                                            onComentarioAgregado(comentarioTexto)
                                            comentarioTexto = ""
                                            mostrarFormulario = false
                                        }
                                        .addOnFailureListener {
                                            Log.e("ComentarioPreviewItem", "Error al actualizar el mensaje", it)
                                        }
                                }
                                .addOnFailureListener {
                                    Log.e("ComentarioPreviewItem", "Error al agregar el comentario", it)
                                }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar comentario"
                    )
                }
            }
        }
    } else {
        Button(
            onClick = { mostrarFormulario = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Agregar comentario")
        }
    }
}

@Composable
fun ComentarioItem(comentario: Map<String, Any>) {
    val autor = comentario["autor"] as? String ?: "Usuario Anónimo"
    val texto = comentario["texto"] as? String ?: ""
    val fechaHora = comentario["fechaHora"] as? String ?: ""
    val imagenPerfil = comentario["imagenPerfil"] as? String ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = imagenPerfil,
            contentDescription = "Foto de perfil del comentario",
            placeholder = painterResource(R.drawable.sync_problem_24dp_000000_fill0_wght400_grad0_opsz24),
            error = painterResource(R.drawable.person_40dp_000000_fill0_wght400_grad0_opsz40),
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = autor,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = texto,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = fechaHora,
                fontSize = 12.sp,
                color = Color.LightGray
            )
        }
    }
}











