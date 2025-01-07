package br.com.brainize.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ConfigurationsViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val configCollection = firestore.collection("userConfigurations")

    var carEnabled by mutableStateOf(false)
    var houseEnabled by mutableStateOf(false)
    var notesEnabled by mutableStateOf(false)
    var agendaEnabled by mutableStateOf(false)

    init {
        loadConfigurations()
    }

    private fun loadConfigurations() {
        viewModelScope.launch {
            try {
                val config = getConfigurationsFromFirestore()
                carEnabled = config.carEnabled
                houseEnabled = config.houseEnabled
                notesEnabled = config.notesEnabled
                agendaEnabled = config.agendaEnabled
                Log.d("ConfigurationsViewModel", "Configurations loaded from Firestore: carEnabled = $carEnabled, houseEnabled = $houseEnabled, notesEnabled = $notesEnabled, agendaEnabled = $agendaEnabled")
            } catch (e: Exception) {
                Log.e("ConfigurationsViewModel", "Error loading configurations from Firestore", e)
            }
        }
    }

    private suspend fun getConfigurationsFromFirestore(): UserConfigurations {
        return suspendCancellableCoroutine { continuation ->
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val configDocument = configCollection.document(userId)
                configDocument.get()
                    .addOnSuccessListener { document ->if (document.exists()) {
                        val config = document.toObject(UserConfigurations::class.java) ?: UserConfigurations()
                        continuation.resume(config)
                    } else {
                        continuation.resume(UserConfigurations())
                    }
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } else {
                continuation.resume(UserConfigurations())
            }
        }
    }

    fun saveConfigurations() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val configDocument = configCollection.document(userId)
                val config = UserConfigurations(
                    carEnabled = carEnabled,
                    houseEnabled = houseEnabled,
                    notesEnabled = notesEnabled,
                    agendaEnabled = agendaEnabled
                )
                try {
                    configDocument.set(config).await()
                    Log.d("ConfigurationsViewModel", "Configurations saved to Firestore for user $userId: carEnabled = $carEnabled, houseEnabled = $houseEnabled, notesEnabled = $notesEnabled, agendaEnabled = $agendaEnabled")
                } catch (e: Exception) {
                    Log.e("ConfigurationsViewModel", "Error saving configurations to Firestore for user $userId", e)
                }
            } else {
                Log.e("ConfigurationsViewModel", "User not logged in, cannot save configurations")
            }
        }
    }

    data class UserConfigurations(
        var carEnabled: Boolean = false,
        var houseEnabled: Boolean = false,
        var notesEnabled: Boolean = false,
        var agendaEnabled: Boolean = false
    )
}