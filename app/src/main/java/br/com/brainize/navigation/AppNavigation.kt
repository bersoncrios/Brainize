package br.com.brainize.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.brainize.screens.car.CarScreen
import br.com.brainize.screens.home.HomeScreen
import br.com.brainize.screens.house.HouseScreen
import br.com.brainize.screens.splash.SplashScreen
import br.com.brainize.viewmodel.CarViewModel
import br.com.brainize.viewmodel.HouseViewModel

@Composable
fun AppNavigation(
    carViewModel: CarViewModel,
    houseViewModel: HouseViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = DestinationScreen.SplashScreen.name) {
        composable(DestinationScreen.SplashScreen.name) {
            SplashScreen(navController = navController)
        }

        composable(DestinationScreen.HomeScreen.name) {
            HomeScreen(navController = navController)
        }

        composable(DestinationScreen.CarScreen.name) {
            CarScreen(
                navController = navController,
                viewModel = carViewModel
            )
        }

        composable(DestinationScreen.HouseScreen.name) {
            HouseScreen(
                navController = navController,
                viewModel = houseViewModel
            )
        }
    }
}