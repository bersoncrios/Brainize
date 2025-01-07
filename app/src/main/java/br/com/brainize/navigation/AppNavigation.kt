package br.com.brainize.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.brainize.screens.car.CarScreen
import br.com.brainize.screens.configurations.ConfigurationScreen
import br.com.brainize.screens.home.HomeScreen
import br.com.brainize.screens.house.HouseScreen
import br.com.brainize.screens.login.LoginScreen
import br.com.brainize.screens.login.RegisterScreen
import br.com.brainize.screens.notes.NotesScreen
import br.com.brainize.screens.schedules.ScheduleScreen
import br.com.brainize.screens.splash.SplashScreen
import br.com.brainize.viewmodel.CarViewModel
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.HouseViewModel
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.NotesViewModel
import br.com.brainize.viewmodel.ScheduleViewModel

@Composable
fun AppNavigation(
    carViewModel: CarViewModel,
    houseViewModel: HouseViewModel,
    loginViewmodel: LoginViewModel,
    notesViewModel: NotesViewModel,
    scheduleViewModel: ScheduleViewModel,
    configurationViewModel: ConfigurationsViewModel
) {
    val navController = rememberNavController()
    AppNavigation(
        navController,
        carViewModel,
        houseViewModel,
        loginViewmodel,
        notesViewModel,
        scheduleViewModel,
        configurationViewModel
    )
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    carViewModel: CarViewModel,
    houseViewModel: HouseViewModel,
    loginViewmodel: LoginViewModel,
    notesViewModel: NotesViewModel,
    scheduleViewModel: ScheduleViewModel,
    configurationsViewModel: ConfigurationsViewModel
) {
    NavHost(navController = navController, startDestination = DestinationScreen.SplashScreen.route) {
        composable(DestinationScreen.SplashScreen.route) {
            SplashScreen(
                navController = navController,
                loginViewModel = loginViewmodel
            )
        }

        composable(
            route = DestinationScreen.HomeScreen.route,
            arguments = DestinationScreen.HomeScreen.arguments
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            HomeScreen(navController = navController, loginViewModel = loginViewmodel, token = token)
        }

        composable(
            route = DestinationScreen.CarScreen.route,
            arguments = DestinationScreen.CarScreen.arguments
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            CarScreen(navController = navController, viewModel = carViewModel, loginViewModel = loginViewmodel, token = token)
        }

        composable(
            route = DestinationScreen.HouseScreen.route,
            arguments = DestinationScreen.HouseScreen.arguments
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            HouseScreen(navController = navController, viewModel = houseViewModel, loginViewModel = loginViewmodel, token = token)
        }

        composable(
            route = DestinationScreen.NotesScreen.route,
            arguments = DestinationScreen.NotesScreen.arguments
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            NotesScreen(navController = navController, viewModel = notesViewModel, loginViewModel = loginViewmodel, token = token)
        }

        composable(
            route = DestinationScreen.ScheduleScreen.route,
            arguments = DestinationScreen.ScheduleScreen.arguments
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            ScheduleScreen(navController = navController, viewModel = scheduleViewModel, loginViewModel = loginViewmodel, token = token)
        }

        composable(
            route = DestinationScreen.ConfigurationScreen.route,
            arguments = DestinationScreen.ConfigurationScreen.arguments
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            ConfigurationScreen(navController = navController, loginViewModel = loginViewmodel, configurationsViewModel = configurationsViewModel, token = token)
        }

        composable(DestinationScreen.LoginScreen.route) {
            LoginScreen(navController = navController, viewModel = loginViewmodel)
        }

        composable(DestinationScreen.RegisterScreen.route) {
            RegisterScreen(navController = navController, viewModel = loginViewmodel)
        }
    }
}