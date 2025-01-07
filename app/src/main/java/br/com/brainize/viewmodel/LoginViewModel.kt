package br.com.brainize.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.State
import androidx.navigation.NavController
import br.com.brainize.navigation.DestinationScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private val _loginState =
        mutableStateOf<LoginState>(LoginState.Idle)

    val loginState: State<LoginState>
        get() = _loginState

    private var _completeName = mutableStateOf("")
    val completeName: String
        get() = _completeName.value


    fun logout(navController: NavController) {
        auth.signOut()
        navController.navigate(DestinationScreen.LoginScreen.route)
        _loginState.value = LoginState.Idle
    }

    fun getCurrentUser() = auth.currentUser

    fun hasLoggedUser(): Boolean = com.google.firebase.ktx.Firebase.auth.currentUser != null

    suspend fun getUserByUID(uid: String): String {
        return suspendCancellableCoroutine { continuation ->
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("completeName") ?: ""
                        _completeName.value = name
                        continuation.resume(name)} else {
                        continuation.resume("")
                    }
                }
                .addOnFailureListener {
                    continuation.resume("")
                }
        }
    }

    fun loginWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val token = authResult.user?.getIdToken(false)?.await()?.token
                _loginState.value = LoginState.Success(token)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }


    fun createUserWithEmailAndPassword(email: String, password: String, name: String, username: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                val token = user?.getIdToken(false)?.await()?.token

                user?.let {
                    val userMap = hashMapOf(
                        "email" to email,
                        "completeName" to name,
                        "username" to username,
                        "uid" to (auth.currentUser?.uid ?: "não foi possivel resgatar o uid"),
                        "createdAt" to System.currentTimeMillis()
                    )
                    firestore.collection("users").document(it.uid).set(userMap).await()
                }
                _loginState.value = LoginState.Success(token)

            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val token: String?) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}
