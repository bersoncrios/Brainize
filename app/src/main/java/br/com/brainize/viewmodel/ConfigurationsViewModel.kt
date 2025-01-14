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

class ConfigurationsViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val configCollection = firestore
        .collection("userConfigurations")

    private var _carEnabled by mutableStateOf(true)
    val carEnabled: Boolean get() = _carEnabled

    private var _houseEnabled by mutableStateOf(true)
    val houseEnabled: Boolean get() = _houseEnabled

    private var _notesEnabled by mutableStateOf(true)
    val notesEnabled: Boolean get() = _notesEnabled

    private var _agendaEnabled by mutableStateOf(true)
    val agendaEnabled: Boolean get() = _agendaEnabled

    private var _collectionEnabled by mutableStateOf(true)
    val collectionEnabled: Boolean get() = _collectionEnabled

    private var _taskColor by mutableStateOf("#90EE90")
    val taskColor: String get() = _taskColor

    private var _reminderColor by mutableStateOf("#bc60c4")
    val reminderColor: String get() = _reminderColor

    fun loadConfigurations(onConfigLoaded: (UserConfigurations)-> Unit) {
        viewModelScope.launch {
            try {
                val config = getConfigurationsFromFirestore()
                _carEnabled = config.carEnabled
                _houseEnabled = config.houseEnabled
                _notesEnabled = config.notesEnabled
                _agendaEnabled = config.agendaEnabled
                _collectionEnabled = config.collectionEnabled
                _taskColor = config.taskColor
                _reminderColor = config.reminderColor
                onConfigLoaded(config)
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
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val config =
                                document.toObject(UserConfigurations::class.java) ?: UserConfigurations()
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

    fun saveConfigurations(onSaveComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val configDocument = configCollection.document(userId)
                val updates = hashMapOf<String, Any>(
                    "carEnabled" to _carEnabled,
                    "houseEnabled" to _houseEnabled,
                    "notesEnabled" to _notesEnabled,
                    "agendaEnabled" to _agendaEnabled,
                    "collectionEnabled" to _collectionEnabled,
                    "taskColor" to _taskColor,
                    "reminderColor" to _reminderColor
                )
                try {
                    configDocument.update(updates).await()
                    onSaveComplete(true)
                } catch (e: Exception) {
                    Log.e(
                        "ConfigurationsViewModel",
                        "Error saving configurations to Firestore for user $userId",
                        e
                    )
                    onSaveComplete(false)
                }
            } else {
                Log.e("ConfigurationsViewModel", "User not logged in, cannot save configurations")
                onSaveComplete(false)
            }
        }
    }

    fun saveColorConfigurations(onSaveComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val configDocument = configCollection.document(userId)
                val updates = hashMapOf<String, Any>(
                    "taskColor" to _taskColor,
                    "reminderColor" to _reminderColor
                )
                try {
                    configDocument.update(updates).await()
                    onSaveComplete(true)
                } catch (e: Exception) {
                    Log.e(
                        "ConfigurationsViewModel",
                        "Error saving configurations to Firestore for user $userId",
                        e
                    )
                    onSaveComplete(false)
                }
            } else {
                Log.e("ConfigurationsViewModel", "User not logged in, cannot save configurations")
                onSaveComplete(false)
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

    fun setCollectionEnabled(value: Boolean) {
        _collectionEnabled = value
    }

    fun setTaskColor(value: String) {
        _taskColor = value
    }

    fun setReminderColor(value: String) {
        _reminderColor = value
    }

    data class UserConfigurations(
        var carEnabled: Boolean = true,
        var houseEnabled: Boolean = true,
        var notesEnabled: Boolean = true,
        var agendaEnabled: Boolean = true,
        var collectionEnabled: Boolean = true,
        var taskColor: String = "#90EE90",
        var reminderColor: String = "#bc60c4"
    )
}