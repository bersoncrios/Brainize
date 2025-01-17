package br.com.brainize.screens.car

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerAlternateSelectButton
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.CarViewModel
import br.com.brainize.viewmodel.LoginViewModel

@Composable
fun CarScreen(
    navController: NavController,
    viewModel: CarViewModel,
    loginViewModel: LoginViewModel,
    token: String?
) {

    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    var windowClosed by rememberSaveable { mutableStateOf(viewModel.windowClosed) }
    var doorClosed by rememberSaveable { mutableStateOf(viewModel.doorClosed) }
    var hasCar by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.hasCar { hasCarResult ->
            hasCar = hasCarResult
            if (!hasCar) {
                navController.navigate(DestinationScreen.CarRegisterScreen.route)
            }
        }
    }

    if (hasCar) {
        Scaffold(
            topBar = {
                BrainizerTopAppBar(
                    title = stringResource(R.string.my_car_label),
                    onBackClick = { navController.popBackStack() }
                )
            }
        ) { paddingValues ->
            BrainizeScreen(paddingValues = paddingValues) {
                Text(
                    text = "Status do seu \$CARRO", //TODO: mudar para o nome do carro cadastrado
                    modifier = Modifier
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
                            defaultIcon = R.drawable.windowglassopened,
                            alternateIcon = R.drawable.windowglassclosed,
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