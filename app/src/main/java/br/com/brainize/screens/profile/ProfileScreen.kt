package br.com.brainize.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    loginViewModel: LoginViewModel,
    token: String?
) {
    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    val userData by viewModel.userData.collectAsState()
    var openNameDialog by remember { mutableStateOf(false) }
    var openUsernameDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(userData.completeName) }
    var username by remember { mutableStateOf(userData.username) }
    var usernameError by remember { mutableStateOf(false) }
    val usernameExists by viewModel.usernameExists.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }

    LaunchedEffect(userData) {
        name = userData.completeName
        username = userData.username
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.my_profile_label),
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        BrainizeScreen(paddingValues = paddingValues) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_placeholder),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "@${userData.username}",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        IconButton(onClick = { openUsernameDialog = true }) {
                            Icon(
                                Icons.Filled.Edit,
                                "Editar Nome",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = userData.completeName,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        )
                        IconButton(onClick = { openNameDialog = true }) {
                            Icon(
                                Icons.Filled.Edit,
                                "Editar Username",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))
                // Logout Button
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            loginViewModel.logout(navController)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFbc60c4))
                    ) {
                        Text(
                            text = "Sair",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    if (openNameDialog) {
        AlertDialog(
            onDismissRequest = { openNameDialog = false },
            title = {
                Text(
                    text = "Editar Nome",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(
                            text = "Nome",
                            color = Color.White
                        )
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUserName(name)
                        openNameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFbc60c4))
                ) {
                    Text("Salvar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { openNameDialog = false }) {
                    Text(text = "Cancelar", color = Color.White)
                }
            },
            containerColor = Color(0xFF372080)
        )
    }

    if (openUsernameDialog) {
        AlertDialog(
            onDismissRequest = {
                openUsernameDialog = false
                usernameError = false
            },
            title = {
                Text(
                    text = "Editar Username",
                    style = MaterialTheme.typography.headlineSmall,color = Color.White
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = username.lowercase(),
                        onValueChange = {
                            username = it
                            usernameError = false
                            viewModel.checkUsernameExists(it)},
                        label = {
                            Text(
                                text = "Username",
                                color = Color.White
                            )
                        },
                        isError = usernameError
                    )
                    if (usernameError) {
                        Text("Este username já está em uso.", color = Color.Red)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!usernameExists) {
                            viewModel.updateUserUsername(username)
                            openUsernameDialog = false
                        } else {
                            usernameError = true}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFbc60c4))
                ) {
                    Text("Salvar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    openUsernameDialog = false
                    usernameError = false
                }) {
                    Text("Cancelar", color = Color.White)
                }
            },
            containerColor = Color(0xFF372080)
        )
    }
}