package br.com.brainize.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.navigation.DestinationScreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    LaunchedEffect(key1 = true) {
        delay(2000L)
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF372080)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.brainizelogo),
                contentDescription = "Logo Brainize",
                modifier = Modifier
                    .size(150.dp)
            )
        }
    }
}
