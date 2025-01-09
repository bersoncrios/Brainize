package br.com.brainize.screens.schedules

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.Schedule
import br.com.brainize.viewmodel.ScheduleViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.util.Calendar
import java.util.Locale

@Composable
fun ScheduleScreen(navController: NavController, viewModel: ScheduleViewModel, loginViewModel: LoginViewModel, token: String?) {

    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true){
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    val schedules = viewModel.schedules.value
    val openDialog = remember { mutableStateOf(false) }
    val newScheduleTime = remember { mutableStateOf("") }
    val newScheduleDate = remember { mutableStateOf("") }
    val newScheduleName = remember { mutableStateOf("") }
    val newSchedulePriority = remember { mutableStateOf("") }

    val systemUiController = rememberSystemUiController()
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
            newScheduleDate.value = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year)
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
                title = "Meus Horários",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0xFF372080)
                )
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillHeight,
                alpha = 1f
            )
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LazyColumn {
                        items(schedules, key ={ schedule -> schedule.id }) { schedule ->
                            ScheduleItem(schedule = schedule, onDelete = { scheduleId -> viewModel.deleteSchedule(scheduleId)})
                        }
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
                        value = newSchedulePriority.value,
                        onValueChange = { newSchedulePriority.value = it },
                        label = { Text("Prioridade") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.addSchedule(newScheduleTime.value, newScheduleDate.value, newScheduleName.value, newSchedulePriority.value)
                    newScheduleTime.value = ""
                    newScheduleDate.value = ""
                    newScheduleName.value = ""
                    newSchedulePriority.value = ""
                    openDialog.value = false
                }) {
                    Text("Salvar")
                }},
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleItem(schedule: Schedule, onDelete: (String) -> Unit) {
    val dismissState = rememberDismissState()
    val isDismissed = dismissState.isDismissed(DismissDirection.EndToStart)
    AnimatedVisibility(
        visible = !isDismissed,
        exit = fadeOut(animationSpec = tween(durationMillis = 300)),
    ) {
        SwipeToDismiss(
            state =dismissState,
            directions = setOf(DismissDirection.EndToStart),
            background = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .padding(8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(Icons.Filled.Delete, "Excluir Horário", tint = Color.White)
                }
            },
            dismissContent = {
                Card(
                    modifier = Modifier.padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFbc60c4))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = schedule.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Data: ${schedule.date}",
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "Horário: ${schedule.time}",
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "Prioridade: ${schedule.priority}",
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        )
    }
    LaunchedEffect(isDismissed) {
        if (isDismissed) {
            onDelete(schedule.id)
        }
    }
    LaunchedEffect(Unit){
        if(isDismissed){
            dismissState.reset()
        }
    }
}