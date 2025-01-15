package br.com.brainize.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.brainize.viewmodel.RemoteConfigViewModel

class RemoteConfigViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RemoteConfigViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RemoteConfigViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}