package br.com.brainize.screens.notes

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import br.com.brainize.viewmodel.NotesViewModel
import br.com.brainize.viewmodel.Note
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteItem(note: Note, onDelete: (String) -> Unit) {
    val dismissState = rememberDismissState()
    val isDismissed = dismissState.isDismissed(DismissDirection.EndToStart)
    AnimatedVisibility(
        visible = !isDismissed,
        exit = fadeOut(animationSpec = tween(durationMillis = 300)),
    ) {
        SwipeToDismiss(
            state = dismissState,
            directions = setOf(DismissDirection.EndToStart),
            background = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .padding(8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(Icons.Filled.Delete, "Excluir Nota", tint = Color.White)
                }
            },
            dismissContent = {
                Card(
                    modifier = Modifier.padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFbc60c4))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "#${note.sequentialId} - ${note.title}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = note.content,
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                        Text(text = "Tipo: ${note.type}",
                            fontSize = 14.sp,
                            color =  Color(0xFF372080)
                        )
                        if (note.type == "Tarefa") {
                            if (!note.dueDate.isNullOrEmpty() && !note.dueTime.isNullOrEmpty()) {
                                Text(
                                    text = "Conclusão: ${note.dueDate} ${note.dueTime}",
                                    fontSize = 14.sp,
                                    color =  Color(0xFF372080)
                                )
                            }
                        }
                    }
                }
            }
        )
    }
    LaunchedEffect(isDismissed) {
        if (isDismissed) {
            onDelete(note.id)
        }
    }
    LaunchedEffect(Unit){
        if(isDismissed){
            dismissState.reset()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(navController: NavController, viewModel: NotesViewModel, loginViewModel: LoginViewModel, token: String?) {

    if (!loginViewModel.hasLoggedUser() && token.isNullOrEmpty()) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    val notes by viewModel.notes.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val openTypeDialog = remember { mutableStateOf(false) }
    val newNoteTitle = remember { mutableStateOf("") }
    val newNoteContent = remember { mutableStateOf("") }
    val newNoteType = remember { mutableStateOf("Lembrete") }
    val newNoteDueDate = remember { mutableStateOf("") }
    val newNoteDueTime = remember { mutableStateOf("") }
    val context = LocalContext.current
    LaunchedEffect(Unit){
        viewModel.loadNotes()
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = "Meus Lembretes",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                openTypeDialog.value = true
            }) {
                Icon(Icons.Filled.Add, "Nova Nota")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0xFF372080)
                ).padding(paddingValues)
        ) {
            Image(painter = painterResource(id = R.drawable.bg),
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
                    val sortedNotes by remember {
                        derivedStateOf {
                            notes.sortedBy { it.sequentialId }
                        }
                    }
                    LazyColumn {
                        items(sortedNotes, key = { note -> note.id }) { note ->
                            NoteItem(note = note, onDelete = { noteId -> viewModel.deleteNote(noteId) })
                        }
                    }
                }
            }
        }
    }
    if (openTypeDialog.value) {
        AlertDialog(
            onDismissRequest = { openTypeDialog.value = false },
            title = { Text("Tipo de Nota") },
            text = {
                Column {
                    val options = listOf("Lembrete", "Tarefa")
                    val expanded = remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded.value,
                        onExpandedChange = { expanded.value = !expanded.value }
                    ) {
                        OutlinedTextField(
                            value = newNoteType.value,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                            options.forEach { option ->
                                DropdownMenuItem(text = { Text(text = option) }, onClick = {
                                    newNoteType.value = option
                                    expanded.value = false
                                })}
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    openTypeDialog.value = false
                    openDialog.value = true
                }) {
                    Text("Continuar")
                }
            },
            dismissButton = {
                TextButton(onClick = { openTypeDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value =false },
            title = { Text("Adicionar Nota") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newNoteTitle.value,
                        onValueChange = { newNoteTitle.value = it },
                        label = { Text("Título") }
                    )
                    OutlinedTextField(
                        value = newNoteContent.value,
                        onValueChange = { newNoteContent.value = it },
                        label = { Text("Conteúdo") }
                    )
                    if (newNoteType.value == "Tarefa") {
                        TextButton(onClick = {
                            val calendar = Calendar.getInstance()
                            val datePickerDialog = DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    newNoteDueDate.value = String.format(
                                        Locale.getDefault(),
                                        "%02d/%02d/%d",
                                        dayOfMonth,
                                        month + 1,
                                        year
                                    )
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            datePickerDialog.show()
                        }) {
                            Text(
                                text = if (newNoteDueDate.value.isEmpty()) "Selecionar Data" else "Data: ${newNoteDueDate.value}"
                            )
                        }
                        TextButton(onClick = {
                            val calendar = Calendar.getInstance()
                            val timePickerDialog = TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    newNoteDueTime.value = String.format(
                                        Locale.getDefault(),
                                        "%02d:%02d",
                                        hourOfDay,
                                        minute
                                    )
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                            )
                            timePickerDialog.show()
                        }) {
                            Text(
                                text = if (newNoteDueTime.value.isEmpty()) "Selecionar Hora" else "Hora: ${newNoteDueTime.value}"
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.saveNote(
                        newNoteTitle.value,
                        newNoteContent.value,
                        newNoteType.value,
                        if (newNoteType.value == "Tarefa") newNoteDueDate.value else null,
                        if (newNoteType.value == "Tarefa") newNoteDueTime.value else null
                    )
                    newNoteTitle.value = ""
                    newNoteContent.value = ""
                    newNoteDueDate.value = ""
                    newNoteDueTime.value = ""
                    openDialog.value = false
                }) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    openDialog.value = false
                    newNoteDueDate.value = ""
                    newNoteDueTime.value = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}