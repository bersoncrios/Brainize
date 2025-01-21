package br.com.brainize.screens.collection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeFloatingActionButton
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.components.CollectionItem
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.CollectionViewModel
import br.com.brainize.viewmodel.LoginViewModel

@Composable
fun CollectionScreen(
    navController: NavController,
    viewModel: CollectionViewModel,
    loginViewModel: LoginViewModel,
    token: String?
) {
    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    val openDialog = remember { mutableStateOf(false) }
    val collections by viewModel.collections.collectAsState()

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.my_collections_label),
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            BrainizeFloatingActionButton(openDialog = openDialog, title = stringResource(R.string.new_note_label))
        }
    ) { paddingValues ->
        BrainizeScreen(paddingValues = paddingValues) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                LazyVerticalGrid(
                    modifier = Modifier.weight(1f),
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    items(collections) { collection ->
                        CollectionItem(
                            collection = collection,
                            onItemClick = {
                                navController.navigate(DestinationScreen.CollectionItemsScreen.createRoute(token, collection.id))
                            }
                        )
                    }
                }
            }
        }
    }

    if (openDialog.value) {
        CreateCollectionDialog(
            onDismiss = { openDialog.value = false },
            onConfirm = { name ->
                viewModel.saveCollection(name)
                openDialog.value = false
            }
        )
    }
}

@Composable
fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var collectionName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Criar Nova Coleção",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = collectionName,
                    onValueChange = { collectionName = it },
                    label = {
                        Text(
                            text = "Nome da Coleção",
                            color = Color.White
                        )
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(collectionName) },
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
