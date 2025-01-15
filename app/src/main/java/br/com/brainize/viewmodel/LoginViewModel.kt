package br.com.brainize.viewmodel

import android.content.Context
import android.util.Log
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
import br.com.brainize.R
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.states.LoginState
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore
    private var _completeName = mutableStateOf("")

    private val _usernameExists = MutableStateFlow(false)
    val usernameExists: StateFlow<Boolean> = _usernameExists

    private val _loginState =
        mutableStateOf<LoginState>(LoginState.Idle)

    val loginState: State<LoginState>
        get() = _loginState

    fun getCurrentUser() = auth.currentUser

    fun hasLoggedUser(): Boolean = getCurrentUser() != null

    fun logout(navController: NavController) {
        auth.signOut()
        _loginState.value = LoginState.Idle
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    suspend fun getUserByUID(uid: String): String {
        return suspendCancellableCoroutine { continuation ->
            firestore
                .collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("completeName") ?: ""
                        _completeName.value = name
                        continuation.resume(name)
                    } else {
                        continuation.resume("")
                    }
                }
                .addOnFailureListener {
                    continuation.resume("")
                }
        }
    }

    fun loginWithEmailAndPassword(
        emailOrUsername: String,
        password: String,
        context: Context
    ) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val user = getUserByUsername(emailOrUsername)

                if (user != null) {
                    val authResult = auth.signInWithEmailAndPassword(user.email, password).await()
                    val token = authResult.user?.getIdToken(false)?.await()?.token
                    _loginState.value = LoginState.Success(token)
                } else {
                    val authResult = auth.signInWithEmailAndPassword(emailOrUsername, password).await()
                    val token = authResult.user?.getIdToken(false)?.await()?.token
                    _loginState.value = LoginState.Success(token)
                }
            } catch (e: Exception) {
                _loginState.value =
                    LoginState.Error(e.message ?: context.getString(R.string.unknow_error))
            }
        }
    }

    private suspend fun getUserByUsername(username: String): User? {
        return suspendCancellableCoroutine { continuation ->
            firestore.collection(USERS_COLLECTION)
                .whereEqualTo(USERNAME, username).limit(1)
                .get().addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val email = document.getString(EMAIL) ?: ""
                        val uid = document.getString(UID) ?: ""
                        val user = User(email, uid)
                        continuation.resume(user)
                    } else {
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
    }

    fun recoveryPassword(email: String, context: Context) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                auth.sendPasswordResetEmail(email).await()
                _loginState.value = LoginState.Success(null)
            } catch (e: Exception) {
                _loginState.value =
                    LoginState.Error(e.message ?: context.getString(R.string.unknow_error))
            }
        }
    }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        name: String,
        username: String,
        context: Context
    ) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                checkUsernameExists(username.lowercase())
                if (_usernameExists.value) {
                    _loginState.value = LoginState.Error("Este username já está em uso")
                    return@launch
                }
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                val token = user?.getIdToken(false)?.await()?.token

                user?.let {
                    val userMap = hashMapOf(
                        EMAIL to email,
                        COMPLETE_NAME to name,
                        USERNAME to username.lowercase(),
                        UID to (auth.currentUser?.uid ?: context.getString(R.string.dont_possible_recovery_uid)),
                        CREATEDAT to System.currentTimeMillis()
                    )
                    firestore
                        .collection(USERS_COLLECTION)
                        .document(it.uid)
                        .set(userMap)
                        .await()}
                _loginState.value =LoginState.Success(token)

            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: context.getString(R.string.unknow_error))
            }
        }
    }

    fun checkUsernameExists(username: String) {
        viewModelScope.launch {
            val query = firestore.collection("users")
                .whereEqualTo("username", username.lowercase())
                .limit(1)
                .get()
                .await()
            _usernameExists.value = !query.isEmpty
        }
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val EMAIL = "email"
        private const val COMPLETE_NAME = "completeName"
        private const val USERNAME = "username"
        private const val CREATEDAT = "createdAt"
        private const val UID = "uid"
    }
}

data class User(val email: String, val uid: String)