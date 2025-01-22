package br.com.brainize.screens.notes

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.*
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.NotesViewModel
import kotlinx.coroutines.launch
import java.util.*
import java.util.Locale

// Constantes para data e hora
const val DATE_FORMAT = "%02d/%02d/%d"
const val HOUR_FORMAT = "%02d:%02d"
const val TASK_LABEL = "Tarefa"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    navController: NavController,
    viewModel: NotesViewModel,
    loginViewModel: LoginViewModel,
    configurationsViewModel: ConfigurationsViewModel,
    token: String?
) {
    val context = LocalContext.current
    val notes by viewModel.notes.collectAsState()

    var openBottomSheetNoteType = remember { mutableStateOf(false) }
    var openBottomSheetNewNote = remember { mutableStateOf(false) }

    val newNoteTitle = remember { mutableStateOf("") }
    val newNoteContent = remember { mutableStateOf("") }
    val newNoteType = remember { mutableStateOf("") }
    val newNoteTag = remember { mutableStateOf("") }
    val newNoteDueDate = remember { mutableStateOf("") }
    val newNoteDueTime = remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }

    if (!loginViewModel.hasLoggedUser() && token.isNullOrEmpty()) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    LaunchedEffect(Unit) {
        viewModel.loadNotes()
        configurationsViewModel.loadConfigurations {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.my_notes_label),
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            BrainizeFloatingActionButton(
                openDialog = openBottomSheetNoteType ,
                title = stringResource(R.string.new_note_label)
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
                val sortedNotes by remember {
                    derivedStateOf {
                        notes.sortedBy { it.sequentialId }
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = sortedNotes,
                        key = { note -> note.id }
                    ) { note ->
                        val taskColor = getColorFromTaskColor(configurationsViewModel.taskColor)
                        val reminderColor =
                            getColorFromTaskColor(configurationsViewModel.reminderColor)
                        NoteItem(
                            note = note,
                            taskColor = taskColor,
                            reminderColor = reminderColor,
                            onDelete = { noteId -> viewModel.deleteNote(noteId) },
                            onLongPress = {
                                navController.navigate(DestinationScreen.NotesDetailsScreen.createRoute(
                                    token,
                                    note.id
                                ))
                            }
                        )
                    }
                }
            }
        }
    }

    // BottomSheet para selecionar o tipo de nota
    if (openBottomSheetNoteType.value) {
        BottomSheetNoteType(
            openBottomSheet = openBottomSheetNoteType,
            newNoteType = remember { mutableStateOf(newNoteType) },
            onConfirm = {
                openBottomSheetNoteType.value = false
                openBottomSheetNewNote.value = true
            }
        )
    }

    // BottomSheet para criar uma nova nota
    if (openBottomSheetNewNote.value) {
        BottomSheetNewNote(
            openBottomSheet = openBottomSheetNewNote,
            newNoteTitle = newNoteTitle,
            newNoteContent = newNoteContent,
            newNoteType = newNoteType,
            newNoteDueDate = newNoteDueDate,
            newNoteDueTime = newNoteDueTime,
            newNoteTag = newNoteTag,
            viewModel = viewModel,
            context = context,
            onConfirm = {
                openBottomSheetNewNote.value = false
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetNoteType(
    openBottomSheet: MutableState<Boolean>,
    newNoteType: MutableState<MutableState<String>>,
    onConfirm: () -> Unit
) {

    val scaffoldState = rememberBottomSheetScaffoldState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    if (openBottomSheet.value) {
        LaunchedEffect(openBottomSheet.value) {
            coroutineScope.launch {
                scaffoldState.bottomSheetState.expand()
                sheetState.show()
                sheetState.expand()
            }
        }

        BottomSheetScaffold(
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight() // Ajuste a altura dinamicamente
                        .heightIn(min = 128.dp)
                        .padding(16.dp)
                ) {
                    Text(
                    text = stringResource(R.string.note_type_title_alert),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
                    val options = listOf(
                        "Lembrete",
                        "Tarefa"
                    )
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = newNoteType.value.value,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(text = option, color = Color.White) },
                                    onClick = {
                                        newNoteType.value.value = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { openBottomSheet.value = false }) {
                            Text(stringResource(id = R.string.cancel_label), color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                openBottomSheet.value = false
                                onConfirm()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFbc60c4))
                        ) {
                            Text(stringResource(id = R.string.continue_label), color = Color.White)
                        }
                    }
                }
            },
            scaffoldState = scaffoldState,
        ) {}
    }
}

// Função para o BottomSheet de nova nota
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetNewNote(
    openBottomSheet: MutableState<Boolean>,
    newNoteTitle: MutableState<String>,
    newNoteContent: MutableState<String>,
    newNoteType: MutableState<String>,
    newNoteTag: MutableState<String>,
    newNoteDueDate: MutableState<String>,
    newNoteDueTime: MutableState<String>,
    viewModel: NotesViewModel,
    context: Context,
    onConfirm: () -> Unit
) {

    val scaffoldState = rememberBottomSheetScaffoldState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    if (openBottomSheet.value) {
        LaunchedEffect(openBottomSheet.value) {
            coroutineScope.launch {
                scaffoldState.bottomSheetState.expand()
                sheetState.show()
                sheetState.expand()
            }
        }

        BottomSheetScaffold(
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .wrapContentHeight()
                        .heightIn(min = 256.dp)

                ) {
                    OutlinedTextField(
                        value = newNoteTitle.value,
                        onValueChange = { newNoteTitle.value = it },
                        label = { Text(text = stringResource(R.string.dialog_new_anotation_title_label), color = Color.White) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newNoteContent.value,
                        onValueChange = { newNoteContent.value = it },
                        label = { Text(text = stringResource(R.string.dialog_new_anotation_what_remember), color = Color.White) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )

                    OutlinedTextField(
                        value = newNoteTag.value,
                        onValueChange = { newNoteTag.value = it },
                        label = { Text(text = stringResource(R.string.dialog_new_anotation_tag), color = Color.White) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (newNoteType.value == TASK_LABEL) {
                        TextButton(
                            onClick = { showDatePicker(context, newNoteDueDate) }
                        ) {
                            Text(
                                text = if (newNoteDueDate.value.isEmpty()) stringResource(R.string.define_task_date_label) else stringResource(
                                    R.string.done_task_date_label,
                                    newNoteDueDate.value
                                ),
                                color = Color.White
                            )
                        }
                        TextButton(
                            onClick = { showTimePicker(context, newNoteDueTime) }
                        ) {
                            Text(
                                text = if (newNoteDueTime.value.isEmpty()) stringResource(R.string.task_hour_label) else stringResource(
                                    R.string.define_task_hour_label,
                                    newNoteDueTime.value
                                ),
                                color = Color.White
                            )
                        }
                    }
                    Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                    ) {
                    TextButton(onClick = { openBottomSheet.value = false }) {
                        Text(stringResource(R.string.cancel_label), color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            viewModel.createNewNote(
                                title = newNoteTitle.value,
                                content = newNoteContent.value,
                                type = newNoteType.value,
                                tag = newNoteTag.value,
                                dueDate = if (newNoteType.value == TASK_LABEL) newNoteDueDate.value else null,
                                dueTime = if (newNoteType.value == TASK_LABEL) newNoteDueTime.value else null
                            )
                            newNoteTitle.value = ""
                            newNoteContent.value = ""
                            newNoteDueDate.value = ""
                            newNoteDueTime.value = ""
                            openBottomSheet.value = false
                            onConfirm()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFbc60c4))
                    ) {
                        Text(stringResource(R.string.save_label), color = Color.White)
                    }
                }
                }
            },
            scaffoldState = scaffoldState,
            ) {}
    }
}



fun showDatePicker(context: Context, newNoteDueDate: MutableState<String>) {
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog( // Use o DatePickerDialog do SDK
        context,
        { _, year, month, dayOfMonth ->
            newNoteDueDate.value = String.format(
                Locale.getDefault(),
                DATE_FORMAT,
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
}

fun showTimePicker(context: Context, newNoteDueTime: MutableState<String>) {
    val calendar = Calendar.getInstance()
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            newNoteDueTime.value = String.format(
                Locale.getDefault(),
                HOUR_FORMAT,
                hourOfDay,
                minute
            )
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )
    timePickerDialog.show()
}