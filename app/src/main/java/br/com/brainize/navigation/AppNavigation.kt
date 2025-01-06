package br.com.brainize.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.brainize.screens.car.CarScreen
import br.com.brainize.screens.home.HomeScreen
import br.com.brainize.screens.house.HouseScreen
import br.com.brainize.screens.login.LoginScreen
import br.com.brainize.screens.login.RegisterScreen
import br.com.brainize.screens.splash.SplashScreen
import br.com.brainize.viewmodel.CarViewModel
import br.com.brainize.viewmodel.HouseViewModel
import br.com.brainize.viewmodel.LoginViewModel

@Composable
fun AppNavigation(
    carViewModel: CarViewModel,
    houseViewModel: HouseViewModel,
    loginViewmodel: LoginViewModel
) {
    val navController = rememberNavController()
    AppNavigation(navController, carViewModel, houseViewModel, loginViewmodel)
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    carViewModel: CarViewModel,
    houseViewModel: HouseViewModel,
    loginViewmodel: LoginViewModel
) {
    NavHost(navController = navController, startDestination = DestinationScreen.SplashScreen.route) {
        composable(DestinationScreen.SplashScreen.route) {
            SplashScreen(navController = navController)
        }

        composable(
            route = DestinationScreen.HomeScreen.route,
            arguments = DestinationScreen.HomeScreen.arguments
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            HomeScreen(navController = navController, token = token)
        }

        composable(
            route = DestinationScreen.CarScreen.route,
            arguments = DestinationScreen.CarScreen.arguments
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            CarScreen(navController = navController, viewModel = carViewModel, token = token)
        }

        composable(
            route = DestinationScreen.HouseScreen.route,
            arguments = DestinationScreen.HouseScreen.arguments
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            HouseScreen(navController = navController, viewModel = houseViewModel, token = token)
        }

        composable(DestinationScreen.LoginScreen.route) {
            LoginScreen(navController = navController, viewModel = loginViewmodel)
        }

        composable(DestinationScreen.RegisterScreen.route) {
            RegisterScreen(navController = navController, viewModel = loginViewmodel)
        }
    }
}