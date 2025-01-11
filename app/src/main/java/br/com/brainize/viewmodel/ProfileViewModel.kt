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
import com.google.firebase.firestore.Query

data class UserData(
    val completeName: String = "",
    val email: String = "",val username: String = ""
)

class ProfileViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private var _completeName = mutableStateOf("")
    val completeName: String
        get() = _completeName.value

    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData

    private val _usernameExists = MutableStateFlow(false)
    val usernameExists: StateFlow<Boolean> = _usernameExists

    fun getCurrentUser() = auth.currentUser
    fun hasLoggedUser(): Boolean = auth.currentUser != null

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

    fun checkUsernameExists(username: String) {
        viewModelScope.launch {
            val query = firestore.collection("users")
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .await()
            _usernameExists.value = !query.isEmpty
        }
    }

    fun updateUserUsername(username: String) {
        val userId = getCurrentUser()?.uid
        if (userId != null) {
            viewModelScope.launch {
                checkUsernameExists(username)
                if (!_usernameExists.value) {
                    firestore.collection("users").document(userId).update("username", username).await()
                    loadUserData()
                }
            }
        }
    }
}