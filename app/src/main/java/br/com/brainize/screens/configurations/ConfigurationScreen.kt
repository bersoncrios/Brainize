package br.com.brainize.screens.configurations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.LoginViewModel

@Composable
fun ConfigurationScreen (
    navController: NavController,
    loginViewModel: LoginViewModel,
    configurationsViewModel: ConfigurationsViewModel,
    token: String?
) {

    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    var carEnabled by remember { mutableStateOf(false) }
    var houseEnabled by remember { mutableStateOf(false) }
    var notesEnabled by remember { mutableStateOf(false) }
    var agendaEnabled by remember { mutableStateOf(false) }
    var collectionEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        configurationsViewModel.loadConfigurations { config ->
            carEnabled = config.carEnabled
            houseEnabled = config.houseEnabled
            notesEnabled = config.notesEnabled
            agendaEnabled = config.agendaEnabled
            collectionEnabled = config.collectionEnabled
        }
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.configurations_label),
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->

        BrainizeScreen(paddingValues = paddingValues) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ConfigSwitchRow(
                    text = "Carro",
                    isChecked = carEnabled,
                    onCheckedChange = {
                        carEnabled = it
                        configurationsViewModel.setCarEnabled(it)
                        configurationsViewModel.saveConfigurations()
                    }
                )
                ConfigSwitchRow(
                    text = "Casa",
                    isChecked = houseEnabled,
                    onCheckedChange = {
                        houseEnabled = it
                        configurationsViewModel.setHouseEnabled(it)
                        configurationsViewModel.saveConfigurations()
                    }
                )
                ConfigSwitchRow(
                    text = "Notas",
                    isChecked = notesEnabled,
                    onCheckedChange = {
                        notesEnabled = it
                        configurationsViewModel.setNotesEnabled(it)
                        configurationsViewModel.saveConfigurations()
                    })
                ConfigSwitchRow(
                    text = "Agenda",
                    isChecked = agendaEnabled,
                    onCheckedChange = {
                        agendaEnabled = it
                        configurationsViewModel.setAgendaEnabled(it)
                        configurationsViewModel.saveConfigurations()
                    })

                ConfigSwitchRow(
                    text = "Coleções",
                    isChecked = collectionEnabled,
                    onCheckedChange = {
                        collectionEnabled = it
                        configurationsViewModel.setCollectionEnabled(it)
                        configurationsViewModel.saveConfigurations()
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        loginViewModel.logout(navController)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Logout")
                }
            }
        }
    }
}

@Composable
fun ConfigSwitchRow(text: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF673AB7),
                checkedTrackColor = Color(0xFFD1C4E9),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}