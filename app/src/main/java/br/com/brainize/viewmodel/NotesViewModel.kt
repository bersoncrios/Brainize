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
    val content: String = "",
    val type: String = "Lembrete", // "Lembrete" ou "Tarefa"
    val dueDate: String? = null, // Data de conclusão (para Tarefa)
    val dueTime: String? = null,  // Hora de conclusão (para Tarefa)
    val sequentialId: Int = 0 // ID sequencial da nota
)

class NotesViewModel : ViewModel(){
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private var _completeName = mutableStateOf("")
    val completeName: String
        get() = _completeName.value

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {loadNotes()
    }

    private fun getCurrentUser() = auth.currentUser
    fun hasLoggedUser(): Boolean = getCurrentUser() != null

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val token: String?) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    private suspend fun getNextSequentialId(): Int {
        val user = auth.currentUser
        if (user != null) {
            val querySnapshot = firestore.collection("users").document(user.uid).collection("notes")
                .orderBy("sequentialId", com.google.firebase.firestore.Query.Direction.DESCENDING)
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


    fun saveNote(title: String, content: String, type: String, dueDate: String? = null, dueTime: String? = null) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                val nextId = getNextSequentialId()
                val noteId = firestore.collection("users").document(user.uid).collection("notes").document().id
                val note = Note(id = noteId, title = title, content = content, type = type, dueDate = dueDate, dueTime = dueTime, sequentialId = nextId)
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
