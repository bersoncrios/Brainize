package br.com.brainize.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class NotesViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private var _completeName = mutableStateOf("")

    val completeName: String
        get() = _completeName.value

    fun getCurrentUser() = auth.currentUser
    fun hasLoggedUser(): Boolean = com.google.firebase.ktx.Firebase.auth.currentUser != null

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val token: String?) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}
