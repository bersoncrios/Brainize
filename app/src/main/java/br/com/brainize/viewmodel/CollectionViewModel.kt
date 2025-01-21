package br.com.brainize.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brainize.model.Collection
import br.com.brainize.model.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.stateIn

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

    private val _collectionState = MutableStateFlow(Collection())
    val collectionState: StateFlow<Collection> = _collectionState.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = Collection()
    )

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
                        .get().await()

                    val collections = snapshot.documents.map { doc ->
                        Collection(
                            id = doc.id,
                            name = doc.getString("name") ?: ""
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

    fun getCollectionById(collectionId: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                try {
                    Log.d("NotesViewModel", "Fetching note with id: $collectionId")
                    val document = firestore
                        .collection(USER_COLLECTIONS)
                        .document(user.uid)
                        .collection(COLLECTION_COLLECTIONS)
                        .document(collectionId)
                        .get()
                        .await()

                    if (document.exists()) {
                        val collection = document.toObject(Collection::class.java)
                        Log.d("NotesViewModel", "Note found: $collection")
                        _collectionState.value = collection ?: Collection()
                    } else {
                        Log.w("NotesViewModel", "Document with id $collectionId not found")
                        _collectionState.value = Collection()
                    }
                } catch (e: Exception) {
                    Log.e("NotesViewModel", "Error fetching note with id $collectionId", e)
                    _collectionState.value = Collection()
                }
            }
        }
    }
}
