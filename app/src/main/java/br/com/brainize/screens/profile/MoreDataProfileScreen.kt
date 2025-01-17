package br.com.brainize.screens.profile

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import br.com.brainize.viewmodel.ProfileViewModel
import java.util.Calendar
import java.util.Locale

@Composable
fun MoreDataProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    loginViewModel: LoginViewModel,
    token: String?
) {
    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    val userData by viewModel.userData.collectAsState()
    var openGenderDialog by remember { mutableStateOf(false) }
    var openBirthdayDialog by remember { mutableStateOf(false) }
    var gender by remember { mutableStateOf(userData.gender) }
    var birthday by remember { mutableStateOf(userData.birthday) }
    var email by remember { mutableStateOf(userData.email) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    var expandedGenderMenu by remember { mutableStateOf(false) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            birthday = String.format(
                Locale.getDefault(),
                "%02d/%02d/%d",
                dayOfMonth,
                month + 1,
                year
            )
        }, year, month, day
    )

    LaunchedEffect(Unit) {viewModel.loadUserData()
    }

    LaunchedEffect(userData) {
        gender = userData.gender
        birthday = userData.birthday
        email = userData.email
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text =  if (userData.gender.isEmpty()) "Gênero" else userData.gender,
                        modifier = Modifier
                            .clickable {
                                openGenderDialog = true
                            },
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text =  if (userData.birthday .isEmpty()) "Data de nascimento" else userData.birthday,
                            modifier = Modifier
                                .clickable {
                                    openBirthdayDialog = true
                                },
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text =  userData.email,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }

        if (openGenderDialog) {
            AlertDialog(
                onDismissRequest = { openGenderDialog = false },
                title = {
                    Text(text = "Gênero",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                },

                text = {
                    Column {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { gender = it },
                            label = {
                                Text(
                                    text = "Gênero",
                                    color = Color.White
                                )
                            },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id =R.drawable.baseline_arrow_drop_down_24),
                                    contentDescription = "Selecionar Gênero",
                                    modifier = Modifier.clickable { expandedGenderMenu = true },
                                    tint = Color.White,
                                )
                                DropdownMenu(
                                    expanded = expandedGenderMenu,
                                    onDismissRequest = { expandedGenderMenu = false }
                                ) {
                                    DropdownMenuItem(text = { Text("Masculino") }, onClick = {
                                        gender = "Masculino"
                                        expandedGenderMenu = false
                                    })
                                    DropdownMenuItem(text = { Text("Feminino") }, onClick = {
                                        gender = "Feminino"
                                        expandedGenderMenu = false
                                    })
                                    DropdownMenuItem(text = { Text("Outros") }, onClick = {
                                        gender = "Outros"
                                        expandedGenderMenu = false
                                    })
                                }
                            }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateUserSexo(gender)
                            openGenderDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFbc60c4))
                    ) {
                        Text("Salvar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { openGenderDialog = false }) {
                        Text(text = "Cancelar", color = Color.White)
                    }
                },
                containerColor = Color(0xFF372080)
            )
        }

        if (openBirthdayDialog) {
            AlertDialog(
                onDismissRequest = {
                    openBirthdayDialog = false
                },
                title = {
                    Text(
                        text = "Data de nascimento",
                        style = MaterialTheme.typography.headlineSmall,color = Color.White
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = birthday,
                            onValueChange = { },
                            label = {
                                Text(
                                    text = "Data de nascimento",
                                    color = Color.White
                                )
                            },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                    contentDescription = "Selecionar Data",
                                    tint = Color.White,
                                    modifier = Modifier.clickable { datePickerDialog.show() }
                                )
                            }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateUserNascimento(birthday)
                            openBirthdayDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFbc60c4))
                    ) {
                        Text("Salvar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        openBirthdayDialog = false
                    }) {
                        Text("Cancelar", color = Color.White)
                    }
                },
                containerColor = Color(0xFF372080)
            )
        }
    }
}
