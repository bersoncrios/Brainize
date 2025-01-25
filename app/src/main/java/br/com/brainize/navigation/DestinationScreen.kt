package br.com.brainize.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class DestinationScreen(val route: String) {
    object SplashScreen : DestinationScreen("splash_screen")

    object LoginScreen : DestinationScreen("login_screen")

    object ForgotPasswordScreen : DestinationScreen("forgot_password_screen")

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

    object CarRegisterScreen : DestinationScreen("car_register_screen/{token}") {
        fun createRoute(token: String?): String = "car_register_screen/$token"
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

    object SearchPeopleScreen : DestinationScreen("search_people_screen/{token}") {
        fun createRoute(token: String?): String = "search_people_screen/$token"
        val arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    }

    object ListFriendsScreen : DestinationScreen("list_friends_screen/{token}") {
        fun createRoute(token: String?): String = "list_friends_screen/$token"
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

    object ScheduleDetailsScreen : DestinationScreen("schedule_details_screen/{token}/{scheduleId}") {
        fun createRoute(token: String?, scheduleId: String?): String = "schedule_details_screen/$token/$scheduleId"
        val arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    }

    object ScheduleDoneScreen : DestinationScreen("schedule_done_screen/{token}") {
        fun createRoute(token: String?): String = "schedule_done_screen/$token"
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

    object MoreDataProfileScreen : DestinationScreen("more_data_profile_screen/{token}") {
        fun createRoute(token: String?): String = "more_data_profile_screen/$token"
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

    object CollectionItemsScreen : DestinationScreen("collection_items_screen/{token}/{collectionId}") {
        fun createRoute(token: String?, collectionId: String?): String = "collection_items_screen/$token/$collectionId"
        val arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    }

    object MainMenuConfigurationScreen : DestinationScreen("main_menu_configuration_screen/{token}") {
        fun createRoute(token: String?): String = "main_menu_configuration_screen/$token"
        val arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    }

    object AppsColorsConfigurationScreen : DestinationScreen("apps_colors_configuration_screen/{token}") {
        fun createRoute(token: String?): String = "apps_colors_configuration_screen/$token"
        val arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    }

    object NotesDetailsScreen : DestinationScreen("notes_details_screen/{token}/{noteId}") {
        fun createRoute(token: String?, noteId: String?): String = "notes_details_screen/$token/$noteId"
        val arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    }


    companion object {fun fromRoute(route: String?): DestinationScreen = when (route?.substringBefore("/")) {
        SplashScreen.route -> SplashScreen
        HomeScreen.route.replace("{token}", "").substringBefore("/") -> HomeScreen
        CarScreen.route.replace("{token}", "").substringBefore("/") -> CarScreen
        CarRegisterScreen.route.replace("{token}", "").substringBefore("/") -> CarRegisterScreen
        HouseScreen.route.replace("{token}", "").substringBefore("/") -> HouseScreen
        NotesScreen.route.replace("{token}", "").substringBefore("/") -> NotesScreen
        NotesDetailsScreen.route.replace("{token}", "").substringBefore("/") -> NotesDetailsScreen
        ScheduleScreen.route.replace("{token}", "").substringBefore("/") -> ScheduleScreen
        ConfigurationScreen.route.replace("{token}", "").substringBefore("/") -> ConfigurationScreen
        MainMenuConfigurationScreen.route.replace("{token}", "").substringBefore("/") -> MainMenuConfigurationScreen
        AppsColorsConfigurationScreen.route.replace("{token}", "").substringBefore("/") -> AppsColorsConfigurationScreen
        ProfileScreen.route.replace("{token}", "").substringBefore("/") -> ProfileScreen
        MoreDataProfileScreen.route.replace("{token}", "").substringBefore("/") -> MoreDataProfileScreen
        CollectionScreen.route.replace("{token}", "").substringBefore("/") -> CollectionScreen
        CollectionItemsScreen.route.replace("{token}", "").substringBefore("/") -> CollectionItemsScreen
        LoginScreen.route -> LoginScreen
        ForgotPasswordScreen.route -> ForgotPasswordScreen
        RegisterScreen.route -> RegisterScreen
        null -> HomeScreen
        else -> throw IllegalArgumentException("Route $route is not recognized")
    }
    }
}