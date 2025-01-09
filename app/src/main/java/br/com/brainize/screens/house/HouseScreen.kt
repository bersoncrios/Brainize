package br.com.brainize.screens.house

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import br.com.brainize.components.BrainizerAlternateSelectButton
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.HouseViewModel
import br.com.brainize.viewmodel.LoginViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun HouseScreen(navController: NavController, viewModel: HouseViewModel, loginViewModel: LoginViewModel, token: String?) {

    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    var windowClosed by rememberSaveable { mutableStateOf(viewModel.windowClosed) }
    var doorClosed by rememberSaveable { mutableStateOf(viewModel.doorClosed) }

    val systemUiController = rememberSystemUiController()
    LaunchedEffect(Unit) {

    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = "Minha Casa",
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
                Text(
                    text = "Status de sua Casa",
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
                        BrainizerAlternateSelectButton(
                            defaultIcon = R.drawable.openedwindow,
                            alternateIcon = R.drawable.closedwindow,
                            isSelected = windowClosed,
                            onToggle = { selected ->
                                windowClosed = selected
                                viewModel.windowClosed = selected
                                viewModel.saveStatus()
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        BrainizerAlternateSelectButton(
                            defaultIcon = R.drawable.opened,
                            alternateIcon = R.drawable.closed,
                            isSelected = doorClosed,
                            onToggle = { selected ->
                                doorClosed = selected
                                viewModel.doorClosed = selected
                                viewModel.saveStatus()
                            }
                        )
                    }
                }
            }
        }
    }
}