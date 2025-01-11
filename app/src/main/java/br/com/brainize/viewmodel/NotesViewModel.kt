package br.com.brainize.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brainize.model.Note
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.Query.Direction.DESCENDING
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotesViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    private fun getCurrentUser() = auth.currentUser
    fun hasLoggedUser(): Boolean = getCurrentUser() != null

    private val user = getCurrentUser()

    init {
        loadNotes()
    }

    private suspend fun getNextSequentialId(): Int {
        if (user != null) {
            val querySnapshot = firestore
                .collection(USERS_COLLECTION)
                .document(user.uid)
                .collection(NOTES_COLLECTION)
                .orderBy(SEQUENTIALID_LABEL, DESCENDING)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val lastNote = querySnapshot.documents[0].toObject(Note::class.java)
                return (lastNote?.sequentialId ?: 0) + 1
            }
        }
        return 1
    }


    fun saveNote (
        title: String,
        content: String,
        type: String,
        dueDate: String? = null,
        dueTime: String? = null
    ) {
        viewModelScope.launch {
            if (user != null) {
                val nextId = getNextSequentialId()
                val noteId = firestore
                    .collection(USERS_COLLECTION)
                    .document(user.uid)
                    .collection(NOTES_COLLECTION)
                    .document()
                    .id
                val note =
                    Note(
                        id = noteId,
                        title = title,
                        content = content,
                        type = type,
                        dueDate = dueDate,
                        dueTime = dueTime,
                        sequentialId = nextId
                    )
                firestore
                    .collection(USERS_COLLECTION)
                    .document(user.uid)
                    .collection(NOTES_COLLECTION)
                    .document(noteId)
                    .set(note)
                    .await()

                loadNotes()
            }
        }
    }

    fun loadNotes() {
        viewModelScope.launch {
            if (getCurrentUser() != null) {
                val snapshot = firestore
                        .collection(USERS_COLLECTION)
                        .document(user!!.uid)
                        .collection(NOTES_COLLECTION)
                        .get()
                        .await()
                val notesList = snapshot.documents.mapNotNull { document ->
                    document.toObject(Note::class.java)
                }
                _notes.value = notesList
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            if (user != null) {
                firestore.collection(USERS_COLLECTION)
                    .document(user.uid)
                    .collection(NOTES_COLLECTION)
                    .document(noteId)
                    .delete()
                    .await()
                loadNotes()
            }
        }
    }

    companion object {
        private const val NOTES_COLLECTION = "notes"
        private const val USERS_COLLECTION = "users"
        private const val SEQUENTIALID_LABEL = "sequentialId"
    }
}
