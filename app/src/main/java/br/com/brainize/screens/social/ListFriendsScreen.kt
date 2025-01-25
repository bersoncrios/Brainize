package br.com.brainize.screens.social

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.SocialViewModel

@Composable
fun ListFriendsScreen (
    navController: NavController,
    loginViewModel: LoginViewModel,
    socialViewModel: SocialViewModel,
    token: String?
) {

    val currentUser = loginViewModel.getCurrentUser()
    val friendsList by socialViewModel.getFriendsList(currentUser!!).collectAsState(initial = emptyList())

    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }


    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = "Lista de amigos",
                onBackClick = { navController.popBackStack() },
                onShareClick = {}
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(DestinationScreen.SearchPeopleScreen.route) },
                containerColor = Color(0xFF372080),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Buscar pessoas"
                )
            }
        }
    ) { paddingValues ->
        BrainizeScreen(paddingValues = paddingValues) {
            LazyColumn(
                modifier = Modifier.padding(vertical = 64.dp, horizontal = 16.dp)
            ) {
                items(friendsList) { friend ->
                    Text(
                        text = "${friend.completeName} (@${friend.username})",
                        color = Color(0xFF372B4B),
                        modifier = Modifier
                            .padding(bottom = 16.dp)

                    )
                }
            }
        }
    }
}
