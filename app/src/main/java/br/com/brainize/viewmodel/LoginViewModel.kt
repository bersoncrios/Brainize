package br.com.brainize.viewmodel

import android.content.Context
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore
    private var _completeName = mutableStateOf("")

    private val _loginState =
        mutableStateOf<LoginState>(LoginState.Idle)

    val loginState: State<LoginState>
        get() = _loginState

    fun getCurrentUser() = auth.currentUser

    fun hasLoggedUser(): Boolean = getCurrentUser() != null
    
    fun logout(navController: NavController) {
        navController.navigate(DestinationScreen.LoginScreen.route)
        auth.signOut()
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
                        continuation.resume(name)} else {
                        continuation.resume("")
                    }
                }
                .addOnFailureListener {
                    continuation.resume("")
                }
        }
    }

    fun loginWithEmailAndPassword(
        email: String,
        password: String, 
        context: Context
    ) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val authResult = 
                    auth.signInWithEmailAndPassword(email, password).await()
                val token = 
                    authResult.user?.getIdToken(false)?.await()?.token
                _loginState.value = 
                    LoginState.Success(token)
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
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                val token = user?.getIdToken(false)?.await()?.token

                user?.let {
                    val userMap = hashMapOf(
                        EMAIL to email,
                        COMPLETE_NAME to name,
                        USERNAME to username,
                        UID to (auth.currentUser?.uid ?: context.getString(R.string.dont_possible_recovery_uid)),
                        CREATEDAT to System.currentTimeMillis()
                    )
                    firestore
                        .collection(USERS_COLLECTION)
                        .document(it.uid)
                        .set(userMap)
                        .await()
                }
                _loginState.value = LoginState.Success(token)

            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: context.getString(R.string.unknow_error))
            }
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
