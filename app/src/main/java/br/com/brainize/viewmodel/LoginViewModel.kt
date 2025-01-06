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

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _loginState =
        mutableStateOf<LoginState>(LoginState.Idle)

    val loginState: State<LoginState>
        get() = _loginState

    fun loginWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val token = authResult.user?.getIdToken(false)?.await()?.token
                _loginState.value = LoginState.Success(token) // Passa o token para o estado de sucesso
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }
    sealed class LoginState {object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val token: String?) : LoginState() // Inclui o token aqui
        data class Error(val message: String) : LoginState()
    }
}
