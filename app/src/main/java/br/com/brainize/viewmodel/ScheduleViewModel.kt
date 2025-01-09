package br.com.brainize.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class Schedule(
    val id: String = UUID.randomUUID().toString(),
    val time: String = "",
    val date: String = "",
    val name: String = "",
    val priority: String = ""
)

class ScheduleViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private var _completeName = mutableStateOf("")
    val completeName: String
        get() = _completeName.value

    private val _schedules = mutableStateOf<List<Schedule>>(emptyList())
    val schedules: androidx.compose.runtime.State<List<Schedule>> = _schedules

    private fun getCurrentUser() = auth.currentUser
    fun hasLoggedUser(): Boolean = getCurrentUser() != null

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val token: String?) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    fun addSchedule(time: String, date: String, name: String, priority: String) {
        val schedule = Schedule(
            time = time,
            date = date,
            name = name,
            priority = priority
        )
        val userId = getCurrentUser()?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).collection("schedules").add(schedule)
        }
    }

    fun loadSchedules() {
        val userId = getCurrentUser()?.uid
        if (userId != null) {
            viewModelScope.launch {
                val snapshot = firestore.collection("users").document(userId).collection("schedules").get().await()
                val schedulesList = snapshot.documents.mapNotNull { document ->
                    document.toObject(Schedule::class.java)?.copy(id = document.id)
                }
                _schedules.value = schedulesList
            }
        }
    }
    fun deleteSchedule(scheduleId: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                firestore.collection("users").document(user.uid).collection("schedules").document(scheduleId).delete().await()
                loadSchedules()
            }
        }
    }
}