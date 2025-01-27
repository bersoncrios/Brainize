package br.com.brainize.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerSelectButton
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.RemoteConfigViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    configurationsViewModel: ConfigurationsViewModel,
    remoteConfigViewModel: RemoteConfigViewModel,
    token: String?
) {

    val userScore by loginViewModel.userScore.collectAsState()
    var completeName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }


    LaunchedEffect(Unit) {
        val userId = loginViewModel.getCurrentUser()?.uid
        loginViewModel.loadUserScore()
        if (userId != null) {
            completeName = loginViewModel.getUserByUID(userId)
        }
        configurationsViewModel.loadConfigurations {
            isLoading = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        BrainizeScreen(paddingValues = paddingValues) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 28.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.TopEnd)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Score",
                                    tint = Color(0xFFbc60c4),
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = "$userScore XP",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFbc60c4)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Olá, $completeName".uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF372080),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Third Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (remoteConfigViewModel._socialEnable) {
                            BrainizerSelectButton(
                                onClick = {
                                    navController.navigate(DestinationScreen.ListFriendsScreen.route)
                                },
                                icon = R.drawable.brainizelogo,
                            )
                        }

                        if (configurationsViewModel.collectionEnabled && remoteConfigViewModel._collectionEnable) {
                            BrainizerSelectButton(
                                onClick = { navController.navigate(DestinationScreen.CollectionScreen.route) },
                                icon = R.drawable.collection,
                            )
                        }
                    }

                    // Second Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (configurationsViewModel.notesEnabled && remoteConfigViewModel._notesEnable) {
                            BrainizerSelectButton(
                                onClick = { navController.navigate(DestinationScreen.NotesScreen.route) },
                                icon = R.drawable.lembrete,
                            )
                        }

                        if (configurationsViewModel.agendaEnabled && remoteConfigViewModel._scheduleEnable) {
                            BrainizerSelectButton(
                                onClick = { navController.navigate(DestinationScreen.ScheduleScreen.route) },
                                icon = R.drawable.agenda,
                            )
                        }
                    }

                    // First Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (configurationsViewModel.carEnabled && remoteConfigViewModel._carEnable) {
                            BrainizerSelectButton(
                                onClick = { navController.navigate(DestinationScreen.CarScreen.route) },
                                icon = R.drawable.car,
                            )
                        }

                        if (configurationsViewModel.houseEnabled && remoteConfigViewModel._houseEnable) {
                            BrainizerSelectButton(
                                onClick = { navController.navigate(DestinationScreen.HouseScreen.route) },
                                icon = R.drawable.house,
                            )
                        }
                    }

                    // Fourth Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly) {
                        if (remoteConfigViewModel._configurationtioEnable) {
                            BrainizerSelectButton(
                                onClick = { navController.navigate(DestinationScreen.ConfigurationScreen.route) },
                                icon = R.drawable.config,
                            )
                        }

                        if (remoteConfigViewModel._profileEnable) {
                            BrainizerSelectButton(
                                onClick = { navController.navigate(DestinationScreen.ProfileScreen.route) },
                                icon = R.drawable.profile,
                            )
                        }
                    }
                }
            }
        }
    }
}