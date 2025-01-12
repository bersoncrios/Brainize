package br.com.brainize.screens.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.model.Note
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.NotesViewModel

@Composable
fun NotesDetailsScreen(
    navController: NavController,
    viewModel: NotesViewModel,
    loginViewModel: LoginViewModel,
    token: String?,
    noteId: String?
) {
    val noteState by viewModel.noteState.collectAsState()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var dueTime by remember {mutableStateOf("") }
    val scrollState = rememberScrollState()
    var isLoading by remember { mutableStateOf(true) }
    var openTitleDialog by remember { mutableStateOf(false) }
    var openContentDialog by remember { mutableStateOf(false) }
    var openDueDateDialog by remember { mutableStateOf(false) }
    var openDueTimeDialog by remember { mutableStateOf(false) }

    if (!loginViewModel.hasLoggedUser() && token.isNullOrEmpty()) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    LaunchedEffect(noteId) {
        isLoading = true
        if (!noteId.isNullOrEmpty()) {
            viewModel.getNoteById(noteId)
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.note_detail_title, noteState.sequentialId),
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        BrainizeScreen(paddingValues = paddingValues) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    noteState.let { note ->
                        Text(
                            text = stringResource(R.string.note_detail_title, noteState.sequentialId),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (note.type == "Tarefa") {
                                    Color(0xFF90EE90)
                                } else {
                                    Color(0xFFbc60c4)
                                }
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = note.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Black,
                                        modifier = Modifier
                                            .weight(2f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 21.sp
                                    )
                                    IconButton(onClick = {
                                        title = note.title
                                        openTitleDialog = true
                                    }) {
                                        Icon(
                                            Icons.Filled.Edit,
                                            "Editar Título",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = note.content,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Black,
                                        modifier = Modifier.weight(2f),
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp
                                    )
                                    IconButton(onClick = {
                                        content = note.content
                                        openContentDialog = true
                                    }) {
                                        Icon(
                                            Icons.Filled.Edit,
                                            "Editar Conteúdo",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                if (note.type == "Tarefa") {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = note.dueDate ?: "",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.Black,
                                            modifier = Modifier.weight(2f),
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 16.sp
                                        )
                                        IconButton(onClick = {
                                            dueDate = note.dueDate ?: ""
                                            openDueDateDialog = true
                                        }) {
                                            Icon(
                                                Icons.Filled.Edit,
                                                "Editar data Final",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = note.dueTime ?: "",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.Black,
                                            modifier = Modifier.weight(2f),
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 16.sp
                                        )
                                        IconButton(onClick = {
                                            dueTime = note.dueTime ?: ""
                                            openDueTimeDialog = true
                                        }) {
                                            Icon(
                                                Icons.Filled.Edit,
                                                "Editar Hora Final",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (openTitleDialog) {
        AlertDialog(
            onDismissRequest = { openTitleDialog = false},
            title = { Text("Editar Título", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        noteState.let { note ->
                            val updatedNote = note.copy(title = title)
                            viewModel.updateNote(updatedNote)
                        }
                        openTitleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Salvar", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { openTitleDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }
    if (openContentDialog) {
        AlertDialog(
            onDismissRequest = { openContentDialog = false },
            title = { Text("Editar Conteúdo", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Conteúdo", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        noteState.let { note ->
                            val updatedNote = note.copy(content = content)
                            viewModel.updateNote(updatedNote)
                        }
                        openContentDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Salvar", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { openContentDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }
    if (openDueDateDialog) {
        AlertDialog(
            onDismissRequest = { openDueDateDialog = false },
            title = { Text("Editar Data Final", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Data Final", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        noteState.let { note ->
                            val updatedNote = note.copy(dueDate = if (dueDate.isNotBlank()) dueDate else null)
                            viewModel.updateNote(updatedNote)
                        }
                        openDueDateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Salvar", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {TextButton(onClick = { openDueDateDialog = false }) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
            }
            }
        )
    }
    if (openDueTimeDialog) {
        AlertDialog(
            onDismissRequest = { openDueTimeDialog = false },
            title = { Text("Editar Hora Final", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                OutlinedTextField(
                    value = dueTime,
                    onValueChange = { dueTime = it },
                    label = { Text("Hora Final", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        noteState.let { note ->
                            val updatedNote = note.copy(dueTime = if (dueTime.isNotBlank()) dueTime else null)
                            viewModel.updateNote(updatedNote)
                        }
                        openDueTimeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Salvar", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { openDueTimeDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }
}