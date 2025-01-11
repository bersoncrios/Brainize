package br.com.brainize.screens.notes

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.components.NoteItem
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.NotesViewModel
import java.util.Calendar
import java.util.Locale

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NotesScreen (
    navController: NavController,
    viewModel: NotesViewModel,
    loginViewModel: LoginViewModel,
    token: String?
) {

    val notes by viewModel.notes.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val openTypeDialog = remember { mutableStateOf(false) }
    val newNoteTitle = remember { mutableStateOf("") }
    val newNoteContent = remember { mutableStateOf("") }
    val newNoteType = remember { mutableStateOf("Lembrete") }
    val newNoteDueDate = remember { mutableStateOf("") }
    val newNoteDueTime = remember { mutableStateOf("") }

    if (!loginViewModel.hasLoggedUser() && token.isNullOrEmpty()) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    val context = LocalContext.current

    LaunchedEffect(Unit){
        viewModel.loadNotes()
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.my_notes_label),
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                openTypeDialog.value = true
            }) {
                Icon(Icons.Filled.Add, stringResource(R.string.new_note_label))
            }
        }
    ) { paddingValues ->
        BrainizeScreen(paddingValues = paddingValues) {
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