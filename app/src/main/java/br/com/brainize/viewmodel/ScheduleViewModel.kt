package br.com.brainize.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brainize.model.Schedule
import br.com.brainize.utils.UserUtils.Companion.INCREMENT_POINT_DONE_SCHEDULE
import br.com.brainize.utils.UserUtils.Companion.INCREMENT_POINT_NEW_ELEMENT_SIMPLE
import br.com.brainize.utils.incrementUserScore
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ScheduleViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private val _schedules = mutableStateOf<List<Schedule>>(emptyList())
    val schedules: State<List<Schedule>> = _schedules

    private fun getCurrentUser() = auth.currentUser
    fun hasLoggedUser(): Boolean = getCurrentUser() != null

    fun addSchedule(
        time: String,
        date: String,
        name: String,
        priority: String,
        tag: String,
        isDone:Boolean
    ) {
        viewModelScope.launch {
            val schedule = Schedule(
                time = time,
                date = date,
                name = name,
                priority = priority,
                tag = tag,
                done = isDone
            )
            val userId = getCurrentUser()?.uid
            if (userId != null) {
                firestore
                    .collection(USER_COLLECTIONS)
                    .document(userId)
                    .collection(SCHEDULE_COLLECTIONS)
                    .add(schedule)
                incrementUserScore(INCREMENT_POINT_NEW_ELEMENT_SIMPLE, auth, firestore)
                loadSchedules()
            }
        }
    }

    fun loadSchedules() {
        val userId = getCurrentUser()?.uid
        if (userId != null) {
            viewModelScope.launch {
                val snapshot = firestore
                    .collection(USER_COLLECTIONS)
                    .document(userId)
                    .collection(SCHEDULE_COLLECTIONS)
                    .get()
                    .await()
                val schedulesList = snapshot.documents.mapNotNull { document ->
                    document.toObject(Schedule::class.java)?.copy(
                        id = document.id,
                        done = document.getBoolean(IS_DONE_LABEL) ?: false
                    )
                }
                _schedules.value = schedulesList
            }
        }
    }

    fun updateScheduleIsDone(
        scheduleId: String,
        isDone: Boolean
    ) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                firestore
                    .collection(USER_COLLECTIONS)
                    .document(user.uid)
                    .collection(SCHEDULE_COLLECTIONS)
                    .document(scheduleId)
                    .update(IS_DONE_LABEL, isDone)
                    .await()
                incrementUserScore(INCREMENT_POINT_DONE_SCHEDULE, auth, firestore)
                loadSchedules()
            }
        }
    }

    fun deleteSchedule(scheduleId: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                firestore
                    .collection(USER_COLLECTIONS)
                    .document(user.uid)
                    .collection(SCHEDULE_COLLECTIONS)
                    .document(scheduleId)
                    .delete()
                    .await()
                loadSchedules()
            }
        }
    }

    fun getSchedulesCount(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                try {
                    val snapshot = firestore
                        .collection(USER_COLLECTIONS)
                        .document(user.uid)
                        .collection(SCHEDULE_COLLECTIONS)
                        .whereEqualTo(IS_DONE_LABEL, false)
                        .get()
                        .await()
                    val count = snapshot.size()
                    onResult(count)
                } catch (e: Exception) {
                    Log.e("ScheduleViewModel", "Error getting schedule count", e)
                    onResult(0)
                }
            } else {
                onResult(0)
            }
        }
    }

    companion object {
        private const val USER_COLLECTIONS = "users"
        private const val SCHEDULE_COLLECTIONS = "schedules"
        private const val IS_DONE_LABEL = "done"
    }
}
