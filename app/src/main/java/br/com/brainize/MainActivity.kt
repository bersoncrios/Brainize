package br.com.brainize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.brainize.database.AppDatabase
import br.com.brainize.navigation.AppNavigation
import br.com.brainize.ui.theme.BrainizeTheme
import br.com.brainize.viewmodel.CarViewModel
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.HouseViewModel
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.NotesViewModel
import br.com.brainize.viewmodel.ProfileViewModel
import br.com.brainize.viewmodel.ScheduleViewModel
import br.com.brainize.viewmodel.factories.CarViewModelFactory
import br.com.brainize.viewmodel.factories.ConfigurationViewModelFactory
import br.com.brainize.viewmodel.factories.HouseViewModelFactory
import br.com.brainize.viewmodel.factories.LoginViewModelFactory
import br.com.brainize.viewmodel.factories.NotesViewModelFactory
import br.com.brainize.viewmodel.factories.ProfileViewModelFactory
import br.com.brainize.viewmodel.factories.ScheduleViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val carViewModel: CarViewModel by viewModels {
            CarViewModelFactory()
        }
        val houseViewModel: HouseViewModel by viewModels {
            HouseViewModelFactory()
        }
        val loginViewModel: LoginViewModel by viewModels {
            LoginViewModelFactory()
        }
        val notesViewModel: NotesViewModel by viewModels {
            NotesViewModelFactory()
        }
        val scheduleViewModel: ScheduleViewModel by viewModels {
            ScheduleViewModelFactory()
        }
        val configurationViewModel: ConfigurationsViewModel by viewModels {
            ConfigurationViewModelFactory()
        }
        val profileViewModel: ProfileViewModel by viewModels {
            ProfileViewModelFactory()
        }

        setContent {

            BrainizeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BrainizeApp(
                        carViewModel = carViewModel,
                        houseViewModel = houseViewModel,
                        loginViewModel = loginViewModel,
                        notesViewModel = notesViewModel,
                        scheduleViewModel = scheduleViewModel,
                        configurationViewModel = configurationViewModel,
                        profileViewModel = profileViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun BrainizeApp(
    carViewModel: CarViewModel,
    houseViewModel: HouseViewModel,
    loginViewModel: LoginViewModel,
    notesViewModel: NotesViewModel,
    scheduleViewModel: ScheduleViewModel,
    configurationViewModel: ConfigurationsViewModel,
    profileViewModel: ProfileViewModel
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppNavigation(
                carViewModel = carViewModel,
                houseViewModel = houseViewModel,
                loginViewmodel = loginViewModel,
                notesViewModel = notesViewModel,
                scheduleViewModel = scheduleViewModel,
                configurationViewModel = configurationViewModel,
                profileViewModel = profileViewModel
            )
        }
    }
}