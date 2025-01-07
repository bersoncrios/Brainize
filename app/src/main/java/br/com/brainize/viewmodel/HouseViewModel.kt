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

class HouseViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val houseStatusCollection = firestore.collection("house")
    var windowClosed by mutableStateOf(true)
    var doorClosed by mutableStateOf(true)

    init {
        loadStatus()
    }

    private fun loadStatus() {
        viewModelScope.launch {
            try {
                val status = getHouseStatusFromFirestore()
                windowClosed = status.windowClosed
                doorClosed = status.doorClosed
            } catch (e: Exception) {
                Log.e("HouseViewModel", "Error loading status from Firestore", e)
            }
        }
    }

    private suspend fun getHouseStatusFromFirestore(): HouseStatus {
        return suspendCancellableCoroutine { continuation ->
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val houseStatusDocument = houseStatusCollection.document(userId)
                houseStatusDocument.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val status = document.toObject(HouseStatus::class.java) ?: HouseStatus(windowClosed = true, doorClosed = true)
                            continuation.resume(status)
                        } else {
                            continuation.resume(HouseStatus(windowClosed = true, doorClosed = true))
                        }
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } else {
                continuation.resume(HouseStatus(windowClosed = true, doorClosed = true))
            }
        }
    }

    fun saveStatus() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val houseStatusDocument = houseStatusCollection.document(userId)
                val status = HouseStatus(windowClosed = windowClosed, doorClosed = doorClosed)
                try {
                    houseStatusDocument.set(status).await()
                    Log.d("HouseViewModel", "Status saved to Firestore for user $userId: windowClosed = $windowClosed, doorClosed = $doorClosed")
                } catch (e: Exception) {
                    Log.e("HouseViewModel", "Error saving status to Firestore for user $userId", e)
                }
            } else {
                Log.e("HouseViewModel", "User not logged in, cannot save status")
            }
        }
    }

    data class HouseStatus(
        var windowClosed: Boolean = true,
        var doorClosed: Boolean = true
    )
}