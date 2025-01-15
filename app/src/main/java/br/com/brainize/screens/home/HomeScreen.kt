package br.com.brainize.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun HomeScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    configurationsViewModel: ConfigurationsViewModel,
    remoteConfigViewModel: RemoteConfigViewModel,
    token: String?
) {

    var completeName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    LaunchedEffect(Unit) {
        val userId = loginViewModel.getCurrentUser()?.uid
        if (userId != null) {
            completeName = loginViewModel.getUserByUID(userId)
        }
        configurationsViewModel.loadConfigurations {
            isLoading = false
        }
    }

    Scaffold { paddingValues ->
        BrainizeScreen(paddingValues = paddingValues) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Olá, ".uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = completeName.uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                        Spacer(modifier = Modifier.width(8.dp))

                        if (configurationsViewModel.houseEnabled && remoteConfigViewModel._houseEnable) {
                            BrainizerSelectButton(
                                onClick = { navController.navigate(DestinationScreen.HouseScreen.route) },
                                icon = R.drawable.house,
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
                                icon = R.drawable.lembretes,
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        if (configurationsViewModel.agendaEnabled && remoteConfigViewModel._scheduleEnable) {
                            BrainizerSelectButton(
                                onClick = { navController.navigate(DestinationScreen.ScheduleScreen.route) },
                                icon = R.drawable.agenda,
                            )
                        }
                    }

                    // Third Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (configurationsViewModel.collectionEnabled && remoteConfigViewModel._collectionEnable) {
                            BrainizerSelectButton(
                                onClick = { navController.navigate(DestinationScreen.CollectionScreen.route) },
                                icon = R.drawable.collection,
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

                        Spacer(modifier = Modifier.width(8.dp))

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