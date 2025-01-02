package br.com.brainize.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brainize.dao.CarStatusDao
import br.com.brainize.model.CarStatus
import kotlinx.coroutines.launch

class CarViewModel(private val dao: CarStatusDao) : ViewModel() {

    var windowClosed by mutableStateOf(true)
    var doorClosed by mutableStateOf(true)

    fun loadStatus() {
        viewModelScope.launch {
            val status = dao.getStatus() ?: CarStatus(0, windowClosed = false, doorClosed = false)
            windowClosed = status.windowClosed
            doorClosed = status.doorClosed
            Log.d("CarViewModel", "Status loaded: windowClosed = $windowClosed, doorClosed = $doorClosed")
        }
    }

    fun saveStatus() {
        viewModelScope.launch {
            val status = CarStatus(id = 1, windowClosed = windowClosed, doorClosed = doorClosed)
            try {
                dao.insertOrUpdate(status)
                Log.d("CarViewModel", "Status saved: windowClosed = $windowClosed, doorClosed = $doorClosed")
            } catch (e: Exception) {
                Log.e("CarViewModel", "Error saving status", e)
            }
        }
    }
}
