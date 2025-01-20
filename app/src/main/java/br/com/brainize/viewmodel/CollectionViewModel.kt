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
                            name = doc.id,
                            id = doc.id
                        )
                    }
                    _collections.value = collections
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
                    val newCollection = Collection(name = collectionName, id = collectionName)
                    firestore.collection(USER_COLLECTIONS).document(user.uid)
                        .collection(COLLECTION_COLLECTIONS)
                        .document(collectionName)
                        .set(newCollection)
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