package br.com.brainize.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class DestinationScreen(val route: String) {
    object SplashScreen : DestinationScreen("splash_screen")

    object LoginScreen : DestinationScreen("login_screen")

    object RegisterScreen : DestinationScreen("register_screen")

    object HomeScreen : DestinationScreen("home_screen/{token}") {
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
    }

    object HouseScreen : DestinationScreen("house_screen/{token}") {
        fun createRoute(token: String?): String = "house_screen/$token"
        val arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    }

    object NotesScreen : DestinationScreen("note_screen/{token}") {
        fun createRoute(token: String?): String = "note_screen/$token"
        val arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    }

    object ScheduleScreen : DestinationScreen("schedule_screen/{token}") {
        fun createRoute(token: String?): String = "schedule_screen/$token"
        val arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    }

    object ConfigurationScreen : DestinationScreen("configuration_screen/{token}") {
        fun createRoute(token: String?): String = "configuration_screen/$token"
        val arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    }

    object ProfileScreen : DestinationScreen("profile_screen/{token}") {
        fun createRoute(token: String?): String = "profile_screen/$token"
        val arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    }

    object CollectionScreen : DestinationScreen("collection_screen/{token}") {
        fun createRoute(token: String?): String = "collection_screen/$token"
        val arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    }


    companion object {fun fromRoute(route: String?): DestinationScreen = when (route?.substringBefore("/")) {
        SplashScreen.route -> SplashScreen
        HomeScreen.route.replace("{token}", "").substringBefore("/") -> HomeScreen
        CarScreen.route.replace("{token}", "").substringBefore("/") -> CarScreen
        HouseScreen.route.replace("{token}", "").substringBefore("/") -> HouseScreen
        NotesScreen.route.replace("{token}", "").substringBefore("/") -> NotesScreen
        ScheduleScreen.route.replace("{token}", "").substringBefore("/") -> ScheduleScreen
        ConfigurationScreen.route.replace("{token}", "").substringBefore("/") -> ConfigurationScreen
        ProfileScreen.route.replace("{token}", "").substringBefore("/") -> ProfileScreen
        CollectionScreen.route.replace("{token}", "").substringBefore("/") -> CollectionScreen
        LoginScreen.route -> LoginScreen
        RegisterScreen.route -> RegisterScreen
        null -> HomeScreen
        else -> throw IllegalArgumentException("Route $route is not recognized")
    }
    }
}