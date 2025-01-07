package br.com.brainize.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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

@Composable
fun HomeScreen(navController: NavController, token: String?) {

    if (token == null) {
        navController.navigate(DestinationScreen.LoginScreen.route)
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
            Text(
                text = "Olá, Guilherme Berson",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BrainizerSelectButton(
                        onClick = { navController.navigate(DestinationScreen.CarScreen.route) },
                        icon = R.drawable.car,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    BrainizerSelectButton(
                        onClick = { navController.navigate(DestinationScreen.HouseScreen.route) },
                        icon = R.drawable.house,
                    )
                }
            }
        }
    }
}

