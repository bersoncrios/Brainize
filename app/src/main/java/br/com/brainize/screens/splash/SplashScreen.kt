package br.com.brainize.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen (
    navController: NavController,
    loginViewModel: LoginViewModel
) {

    LaunchedEffect(key1 = true) {
        delay(2000L)
        if (loginViewModel.hasLoggedUser()) {
            navController.navigate(DestinationScreen.HomeScreen.route)
        } else {
            navController.navigate(DestinationScreen.LoginScreen.route)
        }
    }

    Scaffold { paddingValues ->
        BrainizeScreen(paddingValues = paddingValues) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.brainizelogo),
                    contentDescription = stringResource(R.string.brainize),
                    modifier = Modifier
                        .size(150.dp)
                )
            }
        }
    }
}
