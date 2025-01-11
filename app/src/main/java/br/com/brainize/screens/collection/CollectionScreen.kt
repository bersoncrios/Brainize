package br.com.brainize.screens.collection

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.CollectionViewModel
import br.com.brainize.viewmodel.LoginViewModel

@Composable
fun CollectionScreen (
    navController: NavController,
    viewModel: CollectionViewModel,
    loginViewModel: LoginViewModel,
    token: String?
) {

    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.my_collections_label),
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        BrainizeScreen(paddingValues = paddingValues) {

        }
    }
}
