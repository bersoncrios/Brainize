package br.com.brainize.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = ""
)

class NotesViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private var _completeName = mutableStateOf("")
    val completeName: String
        get() = _completeName.value

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {
        loadNotes()
    }

    private fun getCurrentUser() = auth.currentUser
    fun hasLoggedUser(): Boolean = getCurrentUser() != null

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val token: String?) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    fun saveNote(title: String, content: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                val noteId = firestore.collection("users").document(user.uid).collection("notes").document().id
                val note = Note(id = noteId, title = title, content = content)
                firestore.collection("users").document(user.uid).collection("notes").document(noteId).set(note).await()
                loadNotes()
            }
        }
    }

    fun loadNotes() {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                val snapshot = firestore.collection("users").document(user.uid).collection("notes").get().await()
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
                firestore.collection("users").document(user.uid).collection("notes").document(noteId).delete().await()
                loadNotes()
            }
        }
    }
}