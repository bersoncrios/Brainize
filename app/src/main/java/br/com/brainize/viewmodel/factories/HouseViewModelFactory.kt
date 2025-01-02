package br.com.brainize.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.brainize.dao.HouseStatusDao
import br.com.brainize.viewmodel.HouseViewModel

class HouseViewModelFactory(private val dao: HouseStatusDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HouseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HouseViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}