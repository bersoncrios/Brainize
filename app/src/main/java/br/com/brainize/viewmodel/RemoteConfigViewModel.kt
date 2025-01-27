package br.com.brainize.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brainize.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.launch

class RemoteConfigViewModel : ViewModel() {
    var _carEnable by mutableStateOf(false)
    var _houseEnable by mutableStateOf(false)
    var _notesEnable by mutableStateOf(false)
    var _scheduleEnable by mutableStateOf(false)
    var _collectionEnable by mutableStateOf(false)
    var _configurationtioEnable by mutableStateOf(false)
    var _profileEnable by mutableStateOf(false)
    var _socialEnable by mutableStateOf(false)

    init {
        viewModelScope.launch {
            val firebaseRemoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 1800 // 30 min
            }
            firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings)

            firebaseRemoteConfig.fetchAndActivate()

                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _carEnable = firebaseRemoteConfig.getBoolean("carEnable")
                        _houseEnable = firebaseRemoteConfig.getBoolean("houseEnable")
                        _notesEnable = firebaseRemoteConfig.getBoolean("notesEnable")
                        _scheduleEnable = firebaseRemoteConfig.getBoolean("scheduleEnable")
                        _collectionEnable = firebaseRemoteConfig.getBoolean("collectionEnable")
                        _configurationtioEnable = firebaseRemoteConfig.getBoolean("configurationEnable")
                        _profileEnable = firebaseRemoteConfig.getBoolean("profileEnable")
                        _socialEnable = firebaseRemoteConfig.getBoolean("socialEnable")
                    }
                }
        }
    }
}