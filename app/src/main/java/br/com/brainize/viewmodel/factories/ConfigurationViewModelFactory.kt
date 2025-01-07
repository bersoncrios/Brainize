package br.com.brainize.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.brainize.viewmodel.ConfigurationsViewModel

class ConfigurationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfigurationsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConfigurationsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}