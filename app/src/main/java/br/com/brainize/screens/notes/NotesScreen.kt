package br.com.brainize.screens.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.*
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.NoteSaveResult
import br.com.brainize.viewmodel.NotesViewModel

const val DATE_FORMAT = "%02d/%02d/%d"
const val HOUR_FORMAT = "%02d:%02d"
const val TASK_LABEL = "Tarefa"

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

    // Estado para controlar a exibição do diálogo de erro
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    // Coletando o estado do resultado do salvamento
    val noteSaveResult by viewModel.noteSaveResult.collectAsState()

    if (!loginViewModel.hasLoggedUser() && token.isNullOrEmpty()) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    LaunchedEffect(Unit) {
        viewModel.loadNotes()
        configurationsViewModel.loadConfigurations {
            isLoading = false
        }
    }

    // Observando o resultado do salvamento
    LaunchedEffect(key1 = noteSaveResult) {
        when (noteSaveResult) {
            is NoteSaveResult.Success -> {

                viewModel.resetNoteSaveResult()
            }
            is NoteSaveResult.Error -> {
                val error = noteSaveResult as NoteSaveResult.Error
                dialogMessage = error.message
                showDialog = true
                viewModel.resetNoteSaveResult()
            }
            NoteSaveResult.Idle -> {}
        }
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.my_notes_label),
                onBackClick = { navController.popBackStack() },
                onIconRightClick = {}
            )
        },
        floatingActionButton = {
            BrainizeFloatingActionButton(
                openDialog = openBottomSheetNoteType,
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
                            onClick = {
                                navController.navigate(
                                    DestinationScreen.NotesDetailsScreen.createRoute(
                                        token,
                                        note.id
                                    )
                                )
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
            newNoteTag= newNoteTag,
            viewModel = viewModel,
            context = context,
            onConfirm = {
                openBottomSheetNewNote.value = false
            }
        )
    }

    // Exibindo o diálogo de erro
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