package br.com.brainize.screens.configurations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
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
import br.com.brainize.components.ColorPickerComposeDialog
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.LoginViewModel

@Composable
fun AppsColorsConfigurationScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    configurationsViewModel: ConfigurationsViewModel,
    token: String?
) {

    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    var taskColorHex by remember { mutableStateOf("#FFFFFF") }
    var reminderColorHex by remember { mutableStateOf("#000000") }
    var showTaskColorDialog by remember { mutableStateOf(false) }
    var showReminderColorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        configurationsViewModel.loadConfigurations { config ->
            if (config != null) {
                taskColorHex = config.taskColor
                reminderColorHex = config.reminderColor
            }
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
                Button(onClick = { showTaskColorDialog = true }) {
                    Text("Selecionar Cor da Tarefa")
                }
                Spacer(modifier = Modifier.height(8.dp))
                ColorDisplayCard(colorHex = taskColorHex)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { showReminderColorDialog = true }) {
                    Text("Selecionar Cor do Lembrete")
                }
                Spacer(modifier = Modifier.height(8.dp))
                ColorDisplayCard(colorHex = reminderColorHex)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    configurationsViewModel.setTaskColor(taskColorHex)
                    configurationsViewModel.setReminderColor(reminderColorHex)
                    configurationsViewModel.saveColorConfigurations()
                }) {
                    Text(text = "Salvar Configurações")
                }}
        }
    }
    ColorPickerComposeDialog(
        showDialog = showTaskColorDialog,
        initialColor = taskColorHex,
        title = "Selecione a Cor da Tarefa",
        onColorSelected = { color ->
            taskColorHex = color
            showTaskColorDialog = false
        },
        onDismiss = { showTaskColorDialog = false }
    )
    ColorPickerComposeDialog(
        showDialog = showReminderColorDialog,initialColor = reminderColorHex,
        title = "Selecione a Cor do Lembrete",
        onColorSelected = { color ->
            reminderColorHex = color
            showReminderColorDialog = false
        },
        onDismiss = { showReminderColorDialog = false }
    )
}

@Composable
fun ColorDisplayCard(colorHex: String) {
    val color = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: IllegalArgumentException) {
        Color.Gray // Cor padrão caso a string seja inválida
    }
    Card(
        modifier = Modifier
            .size(60.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color)
        )
    }
}