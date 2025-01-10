package br.com.brainize.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.ProfileViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel, loginViewModel: LoginViewModel, token: String?) {

    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    val systemUiController = rememberSystemUiController()
    val userData = viewModel.userData.collectAsState().value
    val openNameDialog = remember { mutableStateOf(false) }
    val openUsernameDialog = remember { mutableStateOf(false) }
    val name = remember { mutableStateOf(userData.completeName) }
    val username = remember { mutableStateOf(userData.username) }

    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }

    LaunchedEffect(userData) {
        name.value = userData.completeName
        username.value = userData.username
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = "Meu perfil",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0xFF372080)
                )
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Nome: ${userData.completeName}")
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { openNameDialog.value = true }) {
                            Icon(Icons.Filled.Edit, "Editar Nome")
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Username: ${userData.username}")
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { openUsernameDialog.value = true }) {
                            Icon(Icons.Filled.Edit, "Editar Username")
                        }
                    }
                    Text(text = "Email: ${userData.email}")
                }
            }
        }
    }
    if (openNameDialog.value) {
        AlertDialog(
            onDismissRequest = { openNameDialog.value = false },
            title = { Text("Editar Nome") },
            text = {
                OutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Nome") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateUserName(name.value)
                    openNameDialog.value = false
                }) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { openNameDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    if (openUsernameDialog.value) {
        AlertDialog(
            onDismissRequest = { openUsernameDialog.value = false },
            title = {Text("Editar Username") },
            text = {
                OutlinedTextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    label = { Text("Username") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateUserUsername(username.value)
                    openUsernameDialog.value = false
                }) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { openUsernameDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}