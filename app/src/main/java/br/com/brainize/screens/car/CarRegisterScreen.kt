package br.com.brainize.screens.car

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.CarViewModel
import br.com.brainize.viewmodel.LoginViewModel

@Composable
fun CarRegisterScreen(
    navController: NavController,
    viewModel: CarViewModel,
    loginViewModel: LoginViewModel,
    token: String?
) {

    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    var carBrand by remember { mutableStateOf("") }
    var carModel by remember { mutableStateOf("") }
    var carPlate by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.register_car_label),
                onBackClick = {
                    navController.navigate(DestinationScreen.HomeScreen.route)
                }
            )
        }
    ) { paddingValues ->
        BrainizeScreen(paddingValues = paddingValues) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = carBrand,
                    onValueChange = { carBrand = it },
                    label = { Text(text = stringResource(R.string.car_brand_label), color = Color.White) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(8.dp))
                OutlinedTextField(
                    value = carModel,
                    onValueChange = { carModel = it },
                    label = { Text(text = stringResource(R.string.car_model_label), color = Color.White) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(8.dp))
                OutlinedTextField(
                    value = carPlate,
                    onValueChange = { carPlate = it },
                    label = { Text(text = stringResource(R.string.car_plate_label), color = Color.White) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(16.dp))
                Button(
                    onClick = {
                        viewModel.registerCar(carBrand, carModel, carPlate) {
                            navController.navigate(DestinationScreen.CarScreen.route)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.register_label), color = Color.White)
                }
            }
        }
    }
}