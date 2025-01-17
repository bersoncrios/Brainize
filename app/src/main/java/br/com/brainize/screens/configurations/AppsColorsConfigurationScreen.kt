package br.com.brainize.screens.configurations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.components.ColorPickerComposeDialog
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

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
    var priorityHighColorHex by remember { mutableStateOf("#000000") }
    var priorityMediumColorHex by remember { mutableStateOf("#000000") }
    var priorityLowColorHex by remember { mutableStateOf("#000000") }
    var showTaskColorDialog by remember { mutableStateOf(false) }
    var showReminderColorDialog by remember { mutableStateOf(false) }
    var showPriorityHighColorDialog by remember { mutableStateOf(false) }
    var showPriorityMediumColorDialog by remember { mutableStateOf(false) }
    var showPriorityLowColorDialog by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        configurationsViewModel.loadConfigurations { config ->
            if (config != null) {
                taskColorHex = config.taskColor
                reminderColorHex = config.reminderColor
                priorityHighColorHex = config.priorityHighColor
                priorityMediumColorHex = config.priorityMediumColor
                priorityLowColorHex = config.priorityLowColor
            }
        }
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.configurations_label),
                onBackClick = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        BrainizeScreen(paddingValues = paddingValues) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cor da Tarefa
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Cor da Tarefa:", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                ColorDisplayCard(
                    colorHex = taskColorHex,
                    size = 80.dp,
                    onClick = { showTaskColorDialog = true }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Cor do Lembrete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Cor do Lembrete:", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                ColorDisplayCard(
                    colorHex = reminderColorHex,
                    size = 80.dp,
                    onClick = { showReminderColorDialog = true }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Cor da Prioridade Alta
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Cor da Prioridade Alta:", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                ColorDisplayCard(
                    colorHex = priorityHighColorHex,
                    size = 80.dp,
                    onClick = { showPriorityHighColorDialog = true }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Cor da Prioridade Média
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Cor da Prioridade Média:", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                ColorDisplayCard(
                    colorHex = priorityMediumColorHex,
                    size = 80.dp,
                    onClick = { showPriorityMediumColorDialog = true }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Cor da Prioridade Baixa
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Cor da Prioridade Baixa:", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                ColorDisplayCard(
                    colorHex = priorityLowColorHex,
                    size = 80.dp,
                    onClick = { showPriorityLowColorDialog = true }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        isSaving = true
                        configurationsViewModel.setTaskColor(taskColorHex)
                        configurationsViewModel.setReminderColor(reminderColorHex)
                        configurationsViewModel.setPriorityHighColor(priorityHighColorHex)
                        configurationsViewModel.setPriorityMediumColor(priorityMediumColorHex)
                        configurationsViewModel.setPriorityLowColor(priorityLowColorHex)
                        configurationsViewModel.saveColorConfigurations { success ->
                            isSaving = false
                            coroutineScope.launch {
                                if (success) {
                                    snackbarHostState.showSnackbar(
                                        message = "Cores Salvas com Sucesso!",
                                        duration = androidx.compose.material3.SnackbarDuration.Short
                                    )
                                } else {
                                    snackbarHostState.showSnackbar(
                                        message = "Erro ao Salvar as Cores!",
                                        duration = androidx.compose.material3.SnackbarDuration.Short
                                    )}
                            }
                        }
                    },
                    enabled = !isSaving
                ) {
                    if (isSaving) { CircularProgressIndicator(color = Color.White)
                    } else {
                        Text(text = "Salvar Cores")
                    }
                }
            }
        }
    }
    ColorPickerComposeDialog(
        showDialog = showTaskColorDialog,
        initialColor= taskColorHex,
        title = "Selecione a Cor da Tarefa",
        onColorSelected = { color ->
            taskColorHex = color
            showTaskColorDialog = false
        },
        onDismiss = { showTaskColorDialog = false }
    )
    ColorPickerComposeDialog(
        showDialog = showReminderColorDialog,
        initialColor = reminderColorHex,
        title = "Selecione a Cor do Lembrete",
        onColorSelected = { color ->
            reminderColorHex = color
            showReminderColorDialog = false},
        onDismiss = { showReminderColorDialog = false }
    )
    ColorPickerComposeDialog(
        showDialog = showPriorityHighColorDialog,
        initialColor = priorityHighColorHex,
        title = "Selecione a Cor da Prioridade Alta",
        onColorSelected = { color ->
            priorityHighColorHex = color
            showPriorityHighColorDialog = false
        },
        onDismiss = { showPriorityHighColorDialog = false }
    )
    ColorPickerComposeDialog(
        showDialog = showPriorityMediumColorDialog,
        initialColor = priorityMediumColorHex,
        title = "Selecione a Cor da Prioridade Média",
        onColorSelected = { color ->
            priorityMediumColorHex = color
            showPriorityMediumColorDialog = false
        },
        onDismiss = { showPriorityMediumColorDialog = false }
    )
    ColorPickerComposeDialog(
        showDialog = showPriorityLowColorDialog,
        initialColor = priorityLowColorHex,
        title = "Selecione a Cor da Prioridade Baixa",
        onColorSelected = { color ->
            priorityLowColorHex= color
            showPriorityLowColorDialog = false
        },
        onDismiss = { showPriorityLowColorDialog = false }
    )
}

@Composable
fun ColorDisplayCard(colorHex: String, size: Dp, onClick: () -> Unit) {
    val color = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: IllegalArgumentException) {
        Color.Gray
    }
    Card(
        modifier = Modifier
            .size(size)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color)
        )
    }
}