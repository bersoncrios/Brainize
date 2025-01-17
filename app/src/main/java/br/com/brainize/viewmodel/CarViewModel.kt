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

class CarViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val carCollection = firestore.collection("car")
    private val carStatusCollection = firestore.collection("carStatus")

    var windowClosed by mutableStateOf(true)
    var doorClosed by mutableStateOf(true)

    init {
        loadStatus()
    }

    private fun loadStatus() {
        viewModelScope.launch {
            try {
                val status = getCarStatusFromFirestore()
                windowClosed = status.windowClosed
                doorClosed = status.doorClosed
            } catch (e: Exception) {
                Log.e("CarViewModel", "Error loading status from Firestore", e)
            }
        }
    }

    private suspend fun getCarStatusFromFirestore(): CarStatus {
        return suspendCancellableCoroutine { continuation ->
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val carStatusDocument = carStatusCollection.document(userId)
                carStatusDocument.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val status =
                                document.toObject(CarStatus::class.java) ?: CarStatus(
                                    windowClosed = true,
                                    doorClosed = true
                                )
                            continuation.resume(status)
                        } else {
                            continuation.resume(CarStatus(windowClosed = true, doorClosed = true))
                        }
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } else {
                continuation.resume(CarStatus(windowClosed = true, doorClosed = true))
            }
        }
    }


    fun saveStatus() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val carStatusDocument = carStatusCollection.document(userId)
                val status = CarStatus(windowClosed = windowClosed, doorClosed = doorClosed)
                try {
                    carStatusDocument.set(status).await()
                } catch (e: Exception) {
                    Log.e("CarViewModel", "Error saving status to Firestore for user $userId", e)
                }
            } else {
                Log.e("CarViewModel", "User not logged in, cannot save status")
            }
        }
    }

    fun hasCar(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val carDocument = carCollection.document(userId)
                try {
                    val document = carDocument.get().await()
                    onResult(document.exists())
                } catch (e: Exception) {
                    Log.e("CarViewModel", "Error checking car existence for user $userId", e)
                    onResult(false)
                }
            } else {
                onResult(false)
            }
        }
    }

    fun registerCar(carBrand: String, carModel: String, carPlate: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val carData = hashMapOf(
                    "brand" to carBrand,
                    "model" to carModel,
                    "plate" to carPlate
                )
                try {
                    carCollection.document(userId).set(carData).await()
                    onComplete()
                } catch (e: Exception) {
                    Log.e("CarViewModel", "Errorregistering car for user $userId", e)
                }
            } else {
                Log.e("CarViewModel", "User not logged in, cannot register car")
            }
        }
    }


    data class CarStatus(
        var windowClosed: Boolean = true,
        var doorClosed: Boolean = true
    )
}