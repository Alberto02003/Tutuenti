package com.example.tutuenti.pantallas
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import com.example.tutuenti.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tutuenti.metodos.Cabecera
import com.example.tutuenti.metodos.Menu
import com.example.tutuenti.metodos.cerrarSesion
import com.example.tutuenti.metodos.recuperarContraseña
import com.example.tutuenti.metodos.subirImagenFirebase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Configuracion(navController: NavController, cambiarModoOscuro: (Boolean) -> Unit) {
    var nombreUsuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
   var isDarkModeEnabled by remember { mutableStateOf(false) }
    var notificacionesActivadas by remember { mutableStateOf(true) }
    var isMenuVisible by remember { mutableStateOf(false) }
    var buscadorVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var color by remember { mutableStateOf(Color.Red) }
    var imagenPerfilUrl by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imagenUri = uri
        if (uri != null) {
            subirImagenFirebase(
                uri,
                context,
                onSuccess = { imageUrl ->
                    imagenPerfilUrl = imageUrl
                    mensaje = "Imagen subida y mensaje creado exitosamente."
                    color = Color.Green
                },
                onFailure = { errorMessage ->
                    mensaje = errorMessage
                    color = Color.Red
                }
            )
        }
    }

    LaunchedEffect(currentUser?.uid) {
        if (currentUser != null) {
            correo = currentUser.email ?: "Correo no disponible"

            val db = Firebase.firestore
            val userId = currentUser.uid

            db.collection("usuarios").document(userId)
                .addSnapshotListener { document, exception ->
                    if (exception != null) {
                        mensaje = "Error al obtener datos del usuario: ${exception.message}"
                        return@addSnapshotListener
                    }

                    if (document != null && document.exists()) {
                        nombreUsuario = document.getString("nombre") ?: "Nombre no disponible"
                        imagenPerfilUrl = document.getString("imagenPerfilUsuario") ?: ""
                    } else {
                        mensaje = "Usuario no encontrado"
                    }
                }
        } else {
            mensaje = "Usuario no autenticado"
        }
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
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier

                    ) {
                        AsyncImage(
                            model = imagenPerfilUrl,
                            contentDescription = "Foto de perfil",
                            placeholder = painterResource(R.drawable.sync_problem_24dp_000000_fill0_wght400_grad0_opsz24),
                            error = painterResource(R.drawable.person_40dp_000000_fill0_wght400_grad0_opsz40),
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(onClick = { launcher.launch("image/*")}) {
                        Text(text = "Cambiar foto", color = MaterialTheme.colorScheme.primary)
                    }
                }

                OutlinedTextField(
                    value = nombreUsuario,
                    onValueChange = { nombreUsuario = it },
                    label = { Text("Nombre de usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notificaciones",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Switch(
                        checked = notificacionesActivadas,
                        onCheckedChange = { notificacionesActivadas = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Modo oscuro",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isDarkModeEnabled ,
                        onCheckedChange = { cambiarModoOscuro(it);
                                          isDarkModeEnabled = it},
                        thumbContent = {
                            if (isDarkModeEnabled){

                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        cerrarSesion(
                            onSuccess = {
                                navController.navigate("InicioSesion")
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Cerrar sesión", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        recuperarContraseña(
                            email = correo,
                            onSuccess = {
                                mensaje = "Correo de recuperación enviado. Verifica tu bandeja de entrada."
                                color = Color.Green
                            },
                            onError = { error ->
                                mensaje = error
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Cambiar Contraseña", color = Color.White)
                }

                Spacer(modifier = Modifier.height(1.dp))

                if (mensaje.isNotEmpty()) {
                    Text(text = mensaje, color = color, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            when {
                buscadorVisible -> {
                    com.example.tutuenti.metodos.SearchBar(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth(),
                        onCloseSearch = {
                            buscadorVisible = false
                            isMenuVisible = true
                        }, navController = navController
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

