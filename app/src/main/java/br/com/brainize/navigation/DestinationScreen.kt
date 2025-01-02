package br.com.brainize.navigation

enum class DestinationScreen {
    SplashScreen,
    HomeScreen,
    CarScreen,
    HouseScreen;

    companion object {
        fun fromRoute(route: String?): DestinationScreen
                = when(route?.substringBefore("/")) {
            SplashScreen.name -> SplashScreen
            HomeScreen.name -> HomeScreen
            CarScreen.name -> CarScreen
            HouseScreen.name -> HouseScreen
            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}