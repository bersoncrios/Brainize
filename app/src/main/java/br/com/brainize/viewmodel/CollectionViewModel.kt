package br.com.brainize.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.SetOptions

class CollectionViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    companion object {
        const val USER_COLLECTIONS = "users"
        const val COLLECTION_COLLECTIONS = "collections"
    }

    private val _collections = MutableStateFlow<List<Collection>>(emptyList())
    val collections: StateFlow<List<Collection>> = _collections

    init {
        loadCollections()
    }

    private fun loadCollections() {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                try {
                    val snapshot = firestore
                        .collection(USER_COLLECTIONS)
                        .document(user.uid)
                        .collection(COLLECTION_COLLECTIONS)
                        .get()
                        .await()

                    val collections = snapshot.documents.map { doc ->
                        Collection(
                            name = doc.getString("name") ?: "",
                            id = doc.id
                        )
                    }
                    _collections.value= collections
                } catch (e: Exception) {
                    Log.e("CollectionViewModel", "Error loading collections", e)
                }
            }
        }
    }

    fun saveCollection(collectionName: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                try {
                    val newCollectionRef = firestore.collection(USER_COLLECTIONS)
                        .document(user.uid)
                        .collection(COLLECTION_COLLECTIONS)
                        .document()
                    val newCollectionId = newCollectionRef.id

                    val data = hashMapOf(
                        "name" to collectionName,
                        "id" to newCollectionId
                    )

                    newCollectionRef.set(data, SetOptions.merge())
                        .await()

                    loadCollections()
                } catch (e: Exception) {
                    Log.e("CollectionViewModel", "Error saving collection", e)
                }
            }
        }
    }
}

data class Collection(
    val name: String = "",
    val id: String = ""
)