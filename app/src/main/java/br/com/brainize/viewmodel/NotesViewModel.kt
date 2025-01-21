package br.com.brainize.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brainize.model.Note
import br.com.brainize.utils.UserUtils.Companion.DONT_HAVE_SCORE
import br.com.brainize.utils.UserUtils.Companion.INCREMENT_POINT_NEW_ELEMENT_SIMPLE
import br.com.brainize.utils.UserUtils.Companion.INCREMENT_POINT_NEW_ELEMENT_SPECIAL
import br.com.brainize.utils.incrementUserScore
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.Query.Direction.DESCENDING
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotesViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note

    private val _noteState = MutableStateFlow<Note>(Note())
    val noteState: StateFlow<Note> = _noteState.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = Note()
    )

    private fun getCurrentUser() = auth.currentUser
    fun hasLoggedUser(): Boolean = getCurrentUser() != null

    init {
        loadNotes()
    }

    private suspend fun getNextSequentialId(): Int {
        val user = auth.currentUser
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


    fun createNewNote(
        title: String,
        content: String,
        type: String,
        tag: String,
        dueDate: String? = null,
        dueTime: String? = null
    ) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                val nextId = getNextSequentialId()
                val noteId = firestore
                    .collection(USERS_COLLECTION)
                    .document(user.uid)
                    .collection(NOTES_COLLECTION)
                    .document().id
                val note =
                    Note(
                        id = noteId,
                        title = title,
                        content = content,
                        type = type,
                        dueDate = dueDate,
                        dueTime = dueTime,
                        sequentialId = nextId,
                        tag = tag
                    )
                firestore
                    .collection(USERS_COLLECTION)
                    .document(user.uid)
                    .collection(NOTES_COLLECTION)
                    .document(noteId)
                    .set(note)
                    .await()

                if (type == "Tarefa") {
                    incrementUserScore(INCREMENT_POINT_NEW_ELEMENT_SPECIAL, auth, firestore)
                } else if (type == "Lembrete") {
                    incrementUserScore(INCREMENT_POINT_NEW_ELEMENT_SIMPLE, auth, firestore)
                } else {
                    incrementUserScore(DONT_HAVE_SCORE, auth, firestore)
                }
                loadNotes()
            }
        }
    }


    fun loadNotes() {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                val snapshot = firestore
                    .collection(USERS_COLLECTION)
                    .document(user.uid)
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
            val user = auth.currentUser
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

    fun getNoteById(noteId: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                try {
                    Log.d("NotesViewModel", "Fetching note with id: $noteId")
                    val document = firestore
                        .collection(USERS_COLLECTION)
                        .document(user.uid)
                        .collection(NOTES_COLLECTION)
                        .document(noteId)
                        .get()
                        .await()

                    if (document.exists()) {
                        val note = document.toObject(Note::class.java)
                        Log.d("NotesViewModel", "Note found: $note")
                        _noteState.value = note ?: Note()
                    } else {
                        Log.w("NotesViewModel", "Document with id $noteId not found")
                        _noteState.value = Note()
                    }
                } catch (e: Exception) {
                    Log.e("NotesViewModel", "Error fetching note with id $noteId", e)
                    _noteState.value = Note()
                }
            }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                firestore
                    .collection(USERS_COLLECTION)
                    .document(user.uid)
                    .collection(NOTES_COLLECTION)
                    .document(note.id)
                    .set(note)
                    .await()
                getNoteById(note.id)
            }
        }
    }

    fun getNotesCount(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                try {
                    val snapshot = firestore
                        .collection(USERS_COLLECTION)
                        .document(user.uid)
                        .collection(NOTES_COLLECTION)
                        .get()
                        .await()
                    val count = snapshot.size()
                    onResult(count)
                } catch (e: Exception) {
                    Log.e("NotesViewModel", "Error getting notes count", e)
                    onResult(0)
                }
            } else {
                onResult(0)
            }
        }
    }

    companion object {
        private const val NOTES_COLLECTION = "notes"
        private const val USERS_COLLECTION = "users"
        private const val SEQUENTIALID_LABEL = "sequentialId"
    }
}