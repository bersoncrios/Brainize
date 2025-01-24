package br.com.brainize.screens.collection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeFloatingActionButton
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.components.CollectionResourceItem
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.CollectionViewModel
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.model.CollectionItem

@Composable
fun CollectionItemsScreen(
    navController: NavController,
    viewModel: CollectionViewModel,
    loginViewModel: LoginViewModel,
    token: String?,
    collectionId: String?
) {
    val collectionState by viewModel.collectionState.collectAsState()
    val items by viewModel.items.collectAsState()

    var isLoading by remember { mutableStateOf(true) }
    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(collectionId) {
        isLoading = true
        if (!collectionId.isNullOrEmpty()) {
            viewModel.getCollectionById(collectionId)
            viewModel.loadItems(collectionId)
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = collectionState.name,
                onBackClick = { navController.popBackStack() },
                onShareClick = {}
            )
        },
        floatingActionButton = {
            BrainizeFloatingActionButton(openDialog = openDialog, title = stringResource(R.string.new_note_label))
        }
    ) { paddingValues ->
        BrainizeScreen(paddingValues = paddingValues) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Text("Carregando...")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    items(items) { item ->
                        CollectionResourceItem(
                            item = item,
                            onItemClick = {
                                // Lógica para navegar para a tela de detalhes do item
                            }
                        )
                    }
                }
            }
        }
    }

    if (openDialog.value) {
        CreateCollectionItemDialog(
            onDismiss = { openDialog.value = false },
            onConfirm = { name, description ->
                viewModel.saveItem(collectionState.id, name, description)
                openDialog.value = false
            }
        )
    }
}

@Composable
fun CreateCollectionItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Criar Novo Item",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(
                            text = "Nome do Item",
                            color = Color.White
                        )
                    }
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = {
                        Text(
                            text = "Descrição do Item",
                            color = Color.White
                        )
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, description) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFbc60c4))
            ) {
                Text("Salvar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text("Cancelar", color = Color.White)
            }
        },
        containerColor = Color(0xFF372080)
    )
}
