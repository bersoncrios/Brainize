package br.com.brainize.screens.configurations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.components.ColorPickerComposeDialog
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.LoginViewModel

@Composable
fun AppsColorsConfigurationScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    configurationsViewModel: ConfigurationsViewModel,
    token: String?
) {

    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    var primaryColorHex by remember { mutableStateOf("#FFFFFF") }
    var secondaryColorHex by remember { mutableStateOf("#000000") }
    var showPrimaryColorDialog by remember { mutableStateOf(false) }
    var showSecondaryColorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        configurationsViewModel.loadConfigurations { config ->
            if (config != null) {
                primaryColorHex = config.primaryColor
                secondaryColorHex = config.secondaryColor
            }
        }
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.configurations_label),
                onBackClick = { navController.popBackStack() }
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
                Button(onClick = { showPrimaryColorDialog = true }) {
                    Text("Selecionar Cor Primária")
                }
                Text(text = "Cor Primária: $primaryColorHex")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { showSecondaryColorDialog = true }) {
                    Text("Selecionar Cor Secundária")
                }
                Text(text = "Cor Secundária: $secondaryColorHex")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    configurationsViewModel.setPrimaryColor(primaryColorHex)
                    configurationsViewModel.setSecondaryColor(secondaryColorHex)
                    configurationsViewModel.saveColorConfigurations()
                }) {
                    Text(text = "Salvar Configurações")
                }
            }
        }
    }
    ColorPickerComposeDialog(
        showDialog = showPrimaryColorDialog,
        initialColor = primaryColorHex,
        title = "Selecione a Cor Primária",
        onColorSelected = { color ->
            primaryColorHex = color
            showPrimaryColorDialog = false
        },
        onDismiss = { showPrimaryColorDialog = false }
    )
    ColorPickerComposeDialog(
        showDialog = showSecondaryColorDialog,initialColor = secondaryColorHex,
        title = "Selecione a Cor Secundária",
        onColorSelected = { color ->
            secondaryColorHex = color
            showSecondaryColorDialog = false
        },
        onDismiss = { showSecondaryColorDialog = false }
    )
}