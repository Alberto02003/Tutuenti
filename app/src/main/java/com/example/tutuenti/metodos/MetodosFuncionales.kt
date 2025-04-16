package com.example.tutuenti.metodos

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.tutuenti.bd.Comentario
import com.example.tutuenti.bd.Mensaje
import com.example.tutuenti.bd.Usuario
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

fun iniciarSesion(
    correo: String,
    contrasena: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {


    FirebaseAuth.getInstance().signInWithEmailAndPassword(correo, contrasena)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                val mensajeError = when (task.exception) {
                    is FirebaseAuthInvalidUserException -> "El usuario no existe."
                    is FirebaseAuthInvalidCredentialsException -> "Credenciales incorrectas."
                    else -> "Error al iniciar sesión: ${task.exception?.message}"
                }
                onError(mensajeError)
            }
        }
}


fun registrarUsuario(
    email: String,
    password: String,
    nombre: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank() || nombre.isBlank()) {
        onError("Por favor, completa todos los campos.")
        return
    }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: ""
                val usuario = Usuario(
                    id = userId,
                    nombre = nombre,
                    cantidadSeguidores = 0
                )


                firestore.collection("usuarios").document(userId).set(usuario)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onError("Error al guardar datos: ${exception.message}")
                    }
            } else {
                val mensajeError = task.exception?.message ?: "Error desconocido."
                onError(mensajeError)
            }
        }
}

fun cerrarSesion(onSuccess: () -> Unit, onError: (String) -> Unit) {
    try {
        FirebaseAuth.getInstance().signOut()
        onSuccess()
    } catch (e: Exception) {
        onError("Error al cerrar sesión: ${e.message}")
    }
}

fun recuperarContraseña(
    email: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    if (email.isNotBlank()) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.localizedMessage ?: "Error desconocido")
                }
            }
    } else {
        onError("El correo no puede estar vacío.")
    }
}


fun subirImagenFirebase(
    imagenUri: Uri,
    context: Context,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    val storage = Firebase.storage
    val storageRef = storage.reference
    val imageRef = storageRef.child("imagenes_perfil/${UUID.randomUUID()}.jpg")

    imageRef.putFile(imagenUri)
        .addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()

                val user = Firebase.auth.currentUser
                val userId = user?.uid ?: return@addOnSuccessListener

                val db = Firebase.firestore
                val userRef = db.collection("usuarios").document(userId)

                userRef.update("imagenPerfilUsuario", imageUrl)
                    .addOnSuccessListener {
                        onSuccess(imageUrl)
                    }
                    .addOnFailureListener { e ->
                        onFailure("Error al actualizar Firestore: ${e.message}")
                    }
            }.addOnFailureListener { e ->
                onFailure("Error al obtener la URL de la imagen: ${e.message}")
            }
        }
        .addOnFailureListener { e ->
            onFailure("Error al subir la imagen: ${e.message}")
        }
}

fun subirMensaje(
    contenido: String,
    emailRemitente: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val db = Firebase.firestore
    val mensajeId = UUID.randomUUID().toString()
    val remitenteId = emailRemitente.substringBefore("@")

    val userRef = db.collection("usuarios").document(remitenteId)
    userRef.get()
        .addOnSuccessListener { document ->
            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(System.currentTimeMillis()).toString()

            val mensaje = Mensaje(
                mensajeId = mensajeId,
                contenido = contenido,
                remitenteId = remitenteId,
                fechaHora = fechaActual,
            )

            db.collection("mensajes")
                .document(mensajeId)
                .set(mensaje)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onFailure("Error al subir el mensaje: ${e.message}")
                }
        }
        .addOnFailureListener { e ->
            onFailure("Error al obtener la imagen de perfil del usuario: ${e.message}")
        }
}


fun obtenerUsuario(nombreUsuario: String, onSuccess: (Usuario) -> Unit, onFailure: (Exception) -> Unit) {
    val db = Firebase.firestore
    db.collection("usuarios")
        .whereEqualTo("nombre", nombreUsuario)
        .get()
        .addOnSuccessListener { result ->
            if (!result.isEmpty) {
                val usuario = result.documents.firstOrNull()?.toObject(Usuario::class.java)
                if (usuario != null) {
                    onSuccess(usuario)
                } else {
                    onFailure(Exception("Usuario no encontrado"))
                }
            } else {
                onFailure(Exception("Usuario no encontrado"))
            }
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}














