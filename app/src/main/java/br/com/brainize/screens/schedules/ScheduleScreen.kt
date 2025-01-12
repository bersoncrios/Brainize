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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.components.ScheduleItem
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.Schedule
import br.com.brainize.viewmodel.ScheduleViewModel
import java.util.Calendar
import java.util.Locale

@Composable
fun ScheduleScreen(
    navController: NavController,
    viewModel: ScheduleViewModel,
    loginViewModel: LoginViewModel,
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
    var newSchedulePriority by remember { mutableStateOf("") }
    val schedulePriorities = remember { mutableMapOf<String, String>() }
    var expandedPriority by remember { mutableStateOf(false) }


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
                String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year)
        }, year, month, day
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            newScheduleTime.value = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
        }, hour, minute, true
    )

    LaunchedEffect(Unit) {
        viewModel.loadSchedules()
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.my_schedule_label),
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                openDialog.value = true
            }) {
                Icon(Icons.Filled.Add, "Novo Horário")
            }
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(schedules, key = { schedule -> schedule.id }) { schedule ->
                        ScheduleItem(
                            schedule = schedule,
                            onDelete = { scheduleId -> viewModel.deleteSchedule(scheduleId) }
                        )
                    }
                }
            }
        }
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text("Novo Horário") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newScheduleName.value,
                        onValueChange = { newScheduleName.value = it },
                        label = { Text("Nome") }
                    )
                    OutlinedTextField(
                        value = newScheduleDate.value,
                        onValueChange = { },
                        label = { Text("Data") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                contentDescription = "Selecionar Data",
                                modifier = Modifier.clickable { datePickerDialog.show() }
                            )
                        }
                    )
                    OutlinedTextField(
                        value = newScheduleTime.value,
                        onValueChange = { },
                        label = { Text("Horário") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_access_time_24),
                                contentDescription = "Selecionar Horário",
                                modifier = Modifier.clickable { timePickerDialog.show() }
                            )
                        }
                    )
                    OutlinedTextField(
                        value = newSchedulePriority,
                        onValueChange = { },
                        label = { Text("Prioridade") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                                contentDescription = "Selecionar Prioridade",
                                modifier = Modifier.clickable { expandedPriority = true }
                            )
                            DropdownMenu(
                                expanded = expandedPriority,
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
                TextButton(onClick = {
                    viewModel.addSchedule(
                        newScheduleTime.value,
                        newScheduleDate.value,
                        newScheduleName.value,
                        newSchedulePriority
                    )
                    newScheduleTime.value = ""
                    newScheduleDate.value = ""
                    newScheduleName.value = ""
                    newSchedulePriority = ""
                    openDialog.value = false
                }) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}