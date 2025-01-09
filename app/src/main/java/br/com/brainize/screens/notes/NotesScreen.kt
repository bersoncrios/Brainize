package br.com.brainize.screens.notes

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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
                        Text(
                            text = note.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = note.content,
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

    val notes = viewModel.notes.collectAsState().value
    val openDialog = remember { mutableStateOf(false) }
    val newNoteTitle = remember { mutableStateOf("") }
    val newNoteContent = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
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
                openDialog.value = true
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
                    LazyColumn {
                        items(notes, key = { note -> note.id }) { note ->
                            NoteItem(note = note, onDelete = { noteId -> viewModel.deleteNote(noteId) })
                        }
                    }
                }
            }
        }
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
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
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.saveNote(newNoteTitle.value, newNoteContent.value)
                    newNoteTitle.value = ""
                    newNoteContent.value = ""
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