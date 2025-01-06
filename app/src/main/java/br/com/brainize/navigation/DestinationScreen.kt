package br.com.brainize.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class DestinationScreen(val route: String) {
    object SplashScreen : DestinationScreen("splash_screen")object HomeScreen : DestinationScreen("home_screen/{token}") {
        fun createRoute(token: String?): String = "home_screen/$token"
        val arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    }
    object CarScreen : DestinationScreen("car_screen/{token}") {
        fun createRoute(token: String?): String = "car_screen/$token"
        val arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    }object HouseScreen : DestinationScreen("house_screen/{token}") {
        fun createRoute(token: String?): String = "house_screen/$token"
        val arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    }
    object LoginScreen : DestinationScreen("login_screen")

    companion object {fun fromRoute(route: String?): DestinationScreen = when (route?.substringBefore("/")) {
        SplashScreen.route -> SplashScreen
        HomeScreen.route.replace("{token}", "").substringBefore("/") -> HomeScreen
        CarScreen.route.replace("{token}", "").substringBefore("/") -> CarScreen
        HouseScreen.route.replace("{token}", "").substringBefore("/") -> HouseScreen
        LoginScreen.route -> LoginScreen
        null -> HomeScreen
        else -> throw IllegalArgumentException("Route $route is not recognized")
    }
    }
}