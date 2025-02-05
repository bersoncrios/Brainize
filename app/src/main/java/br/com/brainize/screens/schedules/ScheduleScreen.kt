package br.com.brainize.screens.schedules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeFloatingActionButton
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.components.NewScheduleBottomSheet
import br.com.brainize.components.ScheduleItem
import br.com.brainize.components.getColorFromTaskColor
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.NoteSaveResult
import br.com.brainize.viewmodel.ScheduleSaveResult
import br.com.brainize.viewmodel.ScheduleViewModel

@Composable
fun ScheduleScreen(
    navController: NavController,
    viewModel: ScheduleViewModel,
    loginViewModel: LoginViewModel,
    configurationsViewModel: ConfigurationsViewModel,
    token: String?
) {

    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    // Estado para controlar a exibição do diálogo de erro
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    // Coletando o estado do resultado do salvamento
    val scheduleSaveResult by viewModel.scheduleSaveResult.collectAsState()

    val schedules = viewModel.schedules.value
    val openDialog = remember { mutableStateOf(false) }
    var priorityHighColor by remember { mutableStateOf("#873D48") }
    var priorityMediumColor by remember { mutableStateOf("#A6CFD5") }
    var priorityLowColor by remember { mutableStateOf("#90EE90") }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadSchedules()
        configurationsViewModel.loadConfigurations { config ->
            if (config != null) {
                priorityHighColor = config.priorityHighColor
                priorityMediumColor = config.priorityMediumColor
                priorityLowColor = config.priorityLowColor
            }
        }
    }

    LaunchedEffect(key1 = scheduleSaveResult) {
        when (scheduleSaveResult) {
            is ScheduleSaveResult.Success -> {
                viewModel.resetScheduleSaveResult()
            }
            is ScheduleSaveResult.Error -> {
                val error = scheduleSaveResult as ScheduleSaveResult.Error
                dialogMessage = error.message
                showDialog = true
                viewModel.resetScheduleSaveResult()
            }
            ScheduleSaveResult.Idle -> {} // Não faz nada
        }
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.my_schedule_label),
                onBackClick = { navController.popBackStack() },
                rightIcon = Icons.Default.Check,
                hasRightIcon = true,
                onIconRightClick = {
                    navController.navigate(DestinationScreen.ScheduleDoneScreen.createRoute(token))
                }
            )
        },
        floatingActionButton = {
            BrainizeFloatingActionButton(
                openDialog = openDialog,
                title = stringResource(R.string.new_schedule_lavel)
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
                val filteredSchedules = schedules.filter { !it.done }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredSchedules, key = { schedule -> schedule.id }) { schedule ->
                        val priorityHighColor = getColorFromTaskColor(configurationsViewModel.priorityHighColor)
                        val priorityMediumColor = getColorFromTaskColor(configurationsViewModel.priorityMediumColor)
                        val priorityLowColor = getColorFromTaskColor(configurationsViewModel.priorityLowColor)

                        ScheduleItem(
                            schedule = schedule,
                            onIsDoneChange = { scheduleId, isDone -> viewModel.updateScheduleIsDone(scheduleId, isDone) },
                            priorityHighColor = priorityHighColor,
                            priorityMediumColor = priorityMediumColor,
                            priorityLowColor = priorityLowColor,
                            scheduleViewModel = viewModel,
                            onClick = {
                                navController.navigate(DestinationScreen.ScheduleDetailsScreen.createRoute(
                                    token,
                                    schedule.id
                                ))
                            }
                        )
                    }
                }
            }
        }
    }

    if (openDialog.value) {
        NewScheduleBottomSheet(openBottomSheet = openDialog, context = context, viewModel = viewModel)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Erro") },
            text = { Text(dialogMessage) },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}