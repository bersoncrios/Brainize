package br.com.brainize.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserData(
    val completeName: String = "",
    val email: String = "",
    val username: String = ""
)

class ProfileViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private var _completeName = mutableStateOf("")
    val completeName: String
        get() = _completeName.value

    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData

    fun getCurrentUser() = auth.currentUser
    fun hasLoggedUser(): Boolean = auth.currentUser != null

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val token: String?) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    fun loadUserData() {
        val userId = getCurrentUser()?.uid
        if (userId != null) {
            viewModelScope.launch {
                val document = firestore.collection("users").document(userId).get().await()
                if (document.exists()) {
                    val user = document.toObject(UserData::class.java)
                    _userData.value = user ?: UserData()
                }
            }
        }
    }

    fun updateUserName(completeName: String) {
        val userId = getCurrentUser()?.uid
        if (userId != null) {
            viewModelScope.launch {
                firestore.collection("users").document(userId).update("completeName", completeName).await()
                loadUserData()
            }
        }
    }

    fun updateUserUsername(username: String) {
        val userId = getCurrentUser()?.uid
        if (userId != null) {
            viewModelScope.launch {
                firestore.collection("users").document(userId).update("username", username).await()
                loadUserData()
            }
        }
    }
}