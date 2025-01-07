package br.com.brainize.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizerSelectButton
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.LoginViewModel

@Composable
fun HomeScreen(navController: NavController, loginViewModel: LoginViewModel, configurationsViewModel: ConfigurationsViewModel, token: String?) {

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF372080)
            )
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

            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Olá, ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = completeName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // First Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    if (configurationsViewModel.carEnabled) {
                        BrainizerSelectButton(
                            onClick = { navController.navigate(DestinationScreen.CarScreen.route) },
                            icon = R.drawable.car,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    if (configurationsViewModel.houseEnabled) {
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
                    if (configurationsViewModel.notesEnabled) {
                        BrainizerSelectButton(
                            onClick = { navController.navigate(DestinationScreen.NotesScreen.route) },
                            icon = R.drawable.lembretes,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    if (configurationsViewModel.agendaEnabled) {
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
                    BrainizerSelectButton(
                        onClick = { navController.navigate(DestinationScreen.ConfigurationScreen.route) },
                        icon = R.drawable.config,
                    )
                }
            }
        }
    }
}