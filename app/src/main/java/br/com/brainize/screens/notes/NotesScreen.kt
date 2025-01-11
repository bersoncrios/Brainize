package br.com.brainize.screens.notes

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.components.DialogNewNote
import br.com.brainize.components.DialogNoteType
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

    val context = LocalContext.current

    val notes by viewModel.notes.collectAsState()

    val openDialog = remember { mutableStateOf(false) }
    val openTypeDialog = remember { mutableStateOf(false) }

    val newNoteTitle = remember { mutableStateOf("") }
    val newNoteContent = remember { mutableStateOf("") }
    val newNoteType = remember { mutableStateOf("") }
    val newNoteDueDate = remember { mutableStateOf("") }
    val newNoteDueTime = remember { mutableStateOf("") }

    if (!loginViewModel.hasLoggedUser() && token.isNullOrEmpty()) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }


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
            FloatingActionButton(
                onClick = {
                    openTypeDialog.value = true
                }
            ) {
                Icon(
                    Icons.Filled.Add,
                    stringResource(R.string.new_note_label)
                )
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
                        notes.sortedBy {
                            it.sequentialId
                        }
                    }
                }
                LazyColumn {
                    items(
                        items = sortedNotes,
                        key = { note -> note.id }
                    ) { note ->
                        NoteItem(
                            note = note,
                            onDelete = { noteId -> viewModel.deleteNote(noteId) }
                        )
                    }
                }
            }
        }
    }

    if (openTypeDialog.value) {
       DialogNoteType(
           openTypeDialog = openTypeDialog,
           newNoteType = newNoteType,
           openDialog = openDialog
       )
    }

    if (openDialog.value) {
      DialogNewNote(
          openDialog = openDialog,
          newNoteTitle = newNoteTitle,
          newNoteContent = newNoteContent,
          newNoteType = newNoteType,
          newNoteDueDate = newNoteDueDate,
          newNoteDueTime = newNoteDueTime,
          viewModel = viewModel,
          context = context
      )
    }
}