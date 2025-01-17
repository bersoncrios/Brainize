package br.com.brainize.screens.schedules

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeFloatingActionButton
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.components.ScheduleItem
import br.com.brainize.components.getColorFromTaskColor
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.screens.schedules.ScheduleScreenOps.Companion.DATE_FORMAT
import br.com.brainize.screens.schedules.ScheduleScreenOps.Companion.HOUR_FORMAT
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.ScheduleViewModel
import java.util.Calendar
import java.util.Locale

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

    val schedules = viewModel.schedules.value
    val openDialog = remember { mutableStateOf(false) }
    val newScheduleTime = remember { mutableStateOf("") }
    val newScheduleDate = remember { mutableStateOf("") }
    val newScheduleName = remember { mutableStateOf("") }
    val newScheduleTag = remember { mutableStateOf("") }
    var newSchedulePriority by remember { mutableStateOf("") }
    var expandedPriority by remember { mutableStateOf(false) }
    var priorityHighColor by remember { mutableStateOf("#873D48") }
    var priorityMediumColor by remember { mutableStateOf("#A6CFD5") }
    var priorityLowColor by remember { mutableStateOf("#90EE90") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            newScheduleDate.value =
                String.format(
                    Locale.getDefault(),
                    DATE_FORMAT,
                    dayOfMonth,
                    month + 1,year
                )
        }, year, month, day
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            newScheduleTime.value = String.format(
                Locale.getDefault(),
                HOUR_FORMAT,
                hourOfDay,
                minute
            )
        }, hour, minute, true
    )

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

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.my_schedule_label),
                onBackClick = { navController.popBackStack() }
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
                val filteredSchedules = schedules.filter { !it.isDone }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
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
                            priorityLowColor = priorityLowColor
                        )
                    }
                }
            }
        }
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },title = {
                Text(
                    text = "Nova agenda",
                    color = Color.White
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newScheduleName.value,
                        onValueChange = { newScheduleName.value = it },
                        label = {
                            Text(
                                text = "Nome",
                                color = Color.White
                            )
                        }
                    )
                    OutlinedTextField(
                        value = newScheduleTag.value,
                        onValueChange = { newScheduleTag.value = it },
                        label = { Text(
                            text = "Tag",
                            color = Color.White
                        ) }
                    )
                    OutlinedTextField(
                        value = newScheduleDate.value,
                        onValueChange = { },
                        modifier = Modifier
                            .clickable {
                                datePickerDialog.show()
                            },
                        label = { Text(
                            text = "Data",
                            color = Color.White
                        )
                        },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                contentDescription = "Selecionar Data",
                                tint = Color.White,
                                modifier = Modifier.clickable { datePickerDialog.show() }
                            )
                        }
                    )
                    OutlinedTextField(
                        value = newScheduleTime.value,
                        onValueChange = { },
                        label = {
                            Text(
                                text = "Horário",
                                color = Color.White
                            )
                        },
                        readOnly = true,
                        modifier = Modifier
                            .clickable { timePickerDialog.show() },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_access_time_24),
                                contentDescription = "Selecionar Horário",
                                tint = Color.White,
                                modifier = Modifier.clickable { timePickerDialog.show() }
                            )
                        }
                    )
                    OutlinedTextField(
                        value = newSchedulePriority,
                        onValueChange = { },
                        label = {
                            Text(
                                text = "Prioridade",
                                color = Color.White
                            )},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id =R.drawable.baseline_arrow_drop_down_24),
                                contentDescription = "Selecionar Prioridade",
                                modifier = Modifier.clickable { expandedPriority = true },
                                tint = Color.White,
                            )
                            DropdownMenu(
                                expanded =expandedPriority,
                                onDismissRequest = { expandedPriority = false }
                            ) {
                                DropdownMenuItem(text = { Text("Alta") }, onClick = {
                                    newSchedulePriority = "Alta"
                                    expandedPriority = false
                                })
                                DropdownMenuItem(text = { Text("Média") }, onClick = {
                                    newSchedulePriority = "Média"
                                    expandedPriority = false
                                })
                                DropdownMenuItem(text = { Text("Baixa") }, onClick = {
                                    newSchedulePriority = "Baixa"
                                    expandedPriority = false
                                })
                            }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.addSchedule(
                            newScheduleTime.value,
                            newScheduleDate.value,
                            newScheduleName.value,
                            newSchedulePriority,
                            newScheduleTag.value
                        )
                        newScheduleTime.value = ""
                        newScheduleDate.value = ""
                        newScheduleName.value = ""
                        newSchedulePriority = ""
                        newScheduleTag.value = ""
                        openDialog.value = false
                    },
                    colors = ButtonDefaults
                        .buttonColors(
                            containerColor = Color(0xFFbc60c4)
                        )
                ) {
                    Text(
                        text = "Salvar",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text(
                        text = "Cancelar",
                        color = Color.White
                    )
                }
            },
            containerColor = Color(0xFF372080)
        )
    }
}

class ScheduleScreenOps {
    companion object {
        const val DATE_FORMAT = "%02d/%02d/%d"
        const val HOUR_FORMAT = "%02d:%02d"

    }
}