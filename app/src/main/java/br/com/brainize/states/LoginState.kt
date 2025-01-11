package br.com.brainize.states

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String?) : LoginState()
    data class Error(val message: String) : LoginState()
}