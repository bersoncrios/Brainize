package br.com.brainize.screens.notes

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.components.EditDetailItemOnNotesBottomSheet
import br.com.brainize.components.EditDueDateBottomSheet
import br.com.brainize.components.EditDueTimeBottomSheet
import br.com.brainize.components.getColorFromTaskColor
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.NotesViewModel

@Composable
fun NotesDetailsScreen(
    navController: NavController,
    viewModel: NotesViewModel,
    loginViewModel: LoginViewModel,
    configurationsViewModel: ConfigurationsViewModel,
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
    var openTitleDialog = remember { mutableStateOf(false) }
    var openContentDialog = remember { mutableStateOf(false) }
    var openDueDateDialog = remember { mutableStateOf(false) }
    var openDueTimeDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current

    if (!loginViewModel.hasLoggedUser() && token.isNullOrEmpty()) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    LaunchedEffect(noteId) {
        isLoading = true
        if (!noteId.isNullOrEmpty()) {
            viewModel.getNoteById(noteId)
        }
        configurationsViewModel.loadConfigurations {
            isLoading = false
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(
                    R.string.note_detail_title,
                    noteState.sequentialId
                ),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    ) { paddingValues ->
        BrainizeScreen(paddingValues = paddingValues) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    noteState.let { note ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(
                                text = noteState.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(bottom = 16.dp)
                                    .clickable {
                                        title = note.title
                                        openTitleDialog.value = true
                                    }
                            )
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .verticalScroll(scrollState)
                                .clickable {
                                    content = note.content
                                    openContentDialog.value = true
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (note.type == "Tarefa") {
                                    Color(
                                        getColorFromTaskColor(configurationsViewModel.taskColor).value
                                    )
                                } else {
                                    Color(
                                        getColorFromTaskColor(configurationsViewModel.reminderColor).value
                                    )
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)

                            ) {
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
                                            modifier = Modifier
                                                .clickable {
                                                    dueDate = note.dueDate ?: ""
                                                    openDueDateDialog.value = true
                                                },
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 16.sp
                                        )

                                        Text(
                                            text = "às",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.Black,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 16.sp
                                        )

                                        Text(
                                            text = note.dueTime ?: "",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.Black,
                                            modifier = Modifier
                                                .clickable {
                                                    dueTime = note.dueTime ?: ""
                                                    openDueTimeDialog.value = true
                                                },
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 16.sp
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
    if (openTitleDialog.value) {
        EditDetailItemOnNotesBottomSheet(
            openBottomSheet = openTitleDialog,
            viewModel = viewModel,
            item = remember { mutableStateOf(noteState.title) },
            fieldName = "title",
            label = "Editar titulo",
            hint = "Titulo"
        )
    }
    if (openContentDialog.value) {
        EditDetailItemOnNotesBottomSheet(
            openBottomSheet = openContentDialog,
            viewModel = viewModel,
            item = remember { mutableStateOf(noteState.content) },
            fieldName = "content",
            label = "Editar conteúdo",
            hint = "Conteúdo"
        )
    }
    if (openDueDateDialog.value) {
        EditDueDateBottomSheet(
            openBottomSheet = openDueDateDialog,
            viewModel = viewModel,
            context = context,
            noteState = noteState,
            newDuedate = remember { mutableStateOf(noteState.dueDate ?: "") }
        )
    }
    if (openDueTimeDialog.value) {
        EditDueTimeBottomSheet(
            openBottomSheet = openDueTimeDialog,
            viewModel = viewModel,
            context = context,
            noteState = noteState,
            newDueTime = remember { mutableStateOf(noteState.dueTime ?: "") }
        )
    }
}