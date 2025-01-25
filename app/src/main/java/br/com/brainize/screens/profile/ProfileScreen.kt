package br.com.brainize.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import br.com.brainize.viewmodel.NotesViewModel
import br.com.brainize.viewmodel.ProfileViewModel
import br.com.brainize.viewmodel.ScheduleViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    loginViewModel: LoginViewModel,
    notesViewModel: NotesViewModel,
    scheduleViewModel: ScheduleViewModel,
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
    val isUserChecked by loginViewModel.isEmailVerified.collectAsState()
    var userEmailChecked by remember { mutableStateOf(false) }
    var notesCount by remember { mutableStateOf(0) }
    var schedulesCount by remember { mutableStateOf(0) }

    LaunchedEffect(isUserChecked) {
        userEmailChecked = isUserChecked
    }

    LaunchedEffect(Unit) {
        viewModel.loadUserData()
        scheduleViewModel.getSchedulesCount { count ->
            schedulesCount = count
        }
        notesViewModel.getNotesCount { count ->
            notesCount = count
        }
    }
    LaunchedEffect(userData) {
        name = userData.completeName
        username = userData.username
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.my_profile_label),
                onBackClick = { navController.popBackStack() },
                onIconRightClick = {}
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
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "@${userData.username}",
                            modifier = Modifier
                                .clickable {
                                    openUsernameDialog = true
                                },
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF372080)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = userData.completeName,
                            modifier = Modifier
                                .clickable {
                                    openNameDialog = true
                                },
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color =  Color(0xFF372080)
                            )
                        )
                    }

                    // Card de Contagem de Notas e Schedules

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { navController.navigate(DestinationScreen.NotesScreen.route) }
                        ) {
                            Text(
                                text = "Notas",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color =  Color(0xFF372080)
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$notesCount",
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFbc60c4)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(32.dp))

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { navController.navigate(DestinationScreen.ScheduleScreen.route) }
                        ) {
                            Text(
                                text = "Agendas",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color =  Color(0xFF372080)
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$schedulesCount",
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFbc60c4)
                                )
                            )
                        }

                    }

                    if (!userEmailChecked) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "VERIFIQUE SEU ENDEREÇO DE EMAIL".lowercase(),
                                color =Color.Red,
                                style = TextStyle(
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Após verificação, logue novamente no app".lowercase(),
                                color = Color.LightGray,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))

                    // Menu Options as a List

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(DestinationScreen.MoreDataProfileScreen.route)
                                }
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF442c8a)
                        ) {
                            Text(
                                text = "Dados Pessoais",
                                modifier = Modifier.padding(16.dp),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            )
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
                            colors = ButtonDefaults.buttonColors(containerColor =  Color(0xFF372080))
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
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = username.lowercase(),
                            onValueChange = {
                                username = it
                                usernameError = false
                                viewModel.checkUsernameExists(it)
                            },
                            label = {
                                Text(
                                    text = "Username",
                                    color = Color.White
                                )
                            },isError = usernameError
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
                                usernameError = true
                            }
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
}