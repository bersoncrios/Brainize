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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
                onBackClick = { navController.popBackStack() },
                onShareClick = {}
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        BrainizeScreen(paddingValues = paddingValues) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(listOf(
                    "Cor da Tarefa" to taskColorHex,
                    "Cor do Lembrete" to reminderColorHex,
                    "Cor da Prioridade Alta" to priorityHighColorHex,
                    "Cor da Prioridade Média" to priorityMediumColorHex,
                    "Cor da Prioridade Baixa" to priorityLowColorHex
                )) { (texto, cor) ->
                    Card(
                        modifier = Modifier
                            .background(Color.Transparent)
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF372080))
                    ) {
                        Row(
                            modifier = Modifier
                                .background(Color.Transparent)
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = texto,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            ColorDisplayCard(
                                colorHex = cor,
                                size = 64.dp,
                                onClick = {
                                    when (texto) {
                                        "Cor da Tarefa" -> showTaskColorDialog = true
                                        "Cor do Lembrete" -> showReminderColorDialog = true
                                        "Cor da Prioridade Alta" -> showPriorityHighColorDialog = true
                                        "Cor da Prioridade Média" -> showPriorityMediumColorDialog = true
                                        "Cor da Prioridade Baixa" -> showPriorityLowColorDialog = true
                                    }
                                }
                            )
                        }
                    }
                }
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
                                    )
                                }
                            }
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF372080),
                        contentColor = Color(0xFF372080)
                    )
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text(
                            text = "Salvar Cores",
                            color = Color.White
                        )
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