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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import br.com.brainize.components.ConfigSwitchRow
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun MainMenuConfigurationScreen(
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
    var isSaving by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
                onBackClick = { navController.popBackStack() },
                onIconRightClick = {}
            )
        },snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        BrainizeScreen(paddingValues = paddingValues) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ConfigSwitchRow(
                    text = "Carro",
                    isChecked = carEnabled,
                    onCheckedChange = { carEnabled = it }
                )
                ConfigSwitchRow(
                    text = "Casa",
                    isChecked = houseEnabled,
                    onCheckedChange = { houseEnabled = it }
                )
                ConfigSwitchRow(
                    text = "Notas",
                    isChecked = notesEnabled,
                    onCheckedChange = { notesEnabled = it }
                )
                ConfigSwitchRow(
                    text = "Agenda",
                    isChecked = agendaEnabled,
                    onCheckedChange = { agendaEnabled = it }
                )
                ConfigSwitchRow(
                    text = "Coleções",
                    isChecked = collectionEnabled,
                    onCheckedChange = { collectionEnabled = it }
                )
            }

            Column(
                modifier = Modifier
                    .height(128.dp)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        isSaving = true
                        configurationsViewModel.setCarEnabled(carEnabled)
                        configurationsViewModel.setHouseEnabled(houseEnabled)
                        configurationsViewModel.setNotesEnabled(notesEnabled)
                        configurationsViewModel.setAgendaEnabled(agendaEnabled)
                        configurationsViewModel.setCollectionEnabled(collectionEnabled)
                        configurationsViewModel.saveConfigurations { success ->
                            isSaving = false
                            coroutineScope.launch {
                                if (success) {
                                    snackbarHostState.showSnackbar(
                                        message = "Configurações Salvas com Sucesso!",
                                        duration = androidx.compose.material3.SnackbarDuration.Short
                                    )
                                } else {
                                    snackbarHostState.showSnackbar(
                                        message = "Erro ao Salvar as Configurações!",
                                        duration = androidx.compose.material3.SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF372080),
                        contentColor = Color(0xFF372080)
                    ),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text(
                            text = "Salvar Configurações",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}