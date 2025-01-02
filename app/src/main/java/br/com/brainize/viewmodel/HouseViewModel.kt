package br.com.brainize.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brainize.dao.HouseStatusDao
import br.com.brainize.model.HouseStatus
import kotlinx.coroutines.launch

class HouseViewModel(private val dao: HouseStatusDao) : ViewModel() {

    var windowClosed by mutableStateOf(false)
    var doorClosed by mutableStateOf(false)

    fun loadStatus() {
        viewModelScope.launch {
            val status = dao.getStatus() ?: HouseStatus(0, false, false)
            windowClosed = status.windowClosed
            doorClosed = status.doorClosed
        }
    }

    fun saveStatus() {
        viewModelScope.launch {
            val status = HouseStatus(id = 1, windowClosed = windowClosed, doorClosed = doorClosed)
            dao.insertOrUpdate(status)
        }
    }
}
