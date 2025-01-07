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

    private var _carEnabled by mutableStateOf(false)
    val carEnabled: Boolean get() = _carEnabled

    private var _houseEnabled by mutableStateOf(false)
    val houseEnabled: Boolean get() = _houseEnabled

    private var _notesEnabled by mutableStateOf(false)
    val notesEnabled: Boolean get() = _notesEnabled

    private var _agendaEnabled by mutableStateOf(false)
    val agendaEnabled: Boolean get() = _agendaEnabled


    fun loadConfigurations(onConfigLoaded: (UserConfigurations) -> Unit) {
        viewModelScope.launch {
            try {
                val config = getConfigurationsFromFirestore()
                _carEnabled = config.carEnabled
                _houseEnabled = config.houseEnabled
                _notesEnabled = config.notesEnabled
                _agendaEnabled = config.agendaEnabled
                Log.d("ConfigurationsViewModel", "Configurations loaded from Firestore: carEnabled = $_carEnabled, houseEnabled = $_houseEnabled, notesEnabled = $_notesEnabled, agendaEnabled = $_agendaEnabled")
                onConfigLoaded(config)
            } catch (e: Exception) {
                Log.e("ConfigurationsViewModel", "Error loading configurations from Firestore", e)
            }
        }
    }

    private suspend fun getConfigurationsFromFirestore(): UserConfigurations {
        return suspendCancellableCoroutine{ continuation ->
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
                    carEnabled = _carEnabled,
                    houseEnabled = _houseEnabled,
                    notesEnabled = _notesEnabled,
                    agendaEnabled = _agendaEnabled
                )
                try {configDocument.set(config).await()
                    Log.d("ConfigurationsViewModel", "Configurations saved to Firestore for user $userId: carEnabled = $_carEnabled, houseEnabled = $_houseEnabled, notesEnabled = $_notesEnabled, agendaEnabled = $_agendaEnabled")
                } catch (e: Exception) {
                    Log.e("ConfigurationsViewModel", "Error saving configurations to Firestore for user $userId", e)
                }
            } else {
                Log.e("ConfigurationsViewModel", "User not logged in, cannot save configurations")
            }
        }
    }

    fun setCarEnabled(value: Boolean) {
        _carEnabled = value
    }

    fun setHouseEnabled(value: Boolean) {
        _houseEnabled = value
    }

    fun setNotesEnabled(value: Boolean) {
        _notesEnabled = value
    }

    fun setAgendaEnabled(value: Boolean) {
        _agendaEnabled = value
    }

    data class UserConfigurations(
        var carEnabled: Boolean = false,
        var houseEnabled: Boolean = false,
        var notesEnabled: Boolean = false,
        var agendaEnabled: Boolean = false
    )}