package br.com.brainize.screens.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerAlternateSelectButton
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.HouseViewModel
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.SocialViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchPeoplesScreen (
    navController: NavController,
    loginViewModel: LoginViewModel,
    socialViewModel: SocialViewModel,
    token: String?
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val users by socialViewModel.searchUserAndAddFriend(searchQuery).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val currentUser = loginViewModel.getCurrentUser()
    val focusRequester = remember { FocusRequester() }

    if (!loginViewModel.hasLoggedUser() && token?.isEmpty() == true) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus() // Solicite o foco
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = stringResource(R.string.my_house_label),
                onBackClick = { navController.popBackStack() },
                onShareClick = {}
            )
        }
    ) { paddingValues ->
            BrainizeScreen(paddingValues = paddingValues) {

                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Buscar pessoas") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .focusRequester(focusRequester),
                    )
                }

                LazyColumn {
                    items(users) { user ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text(
                                    user.completeName,
                                    color = Color.Black
                                )
                                Text(
                                    user.username,
                                    color = Color.Black
                                )
                            }
                            Button(onClick = {
                                scope.launch {
                                    if (currentUser != null) {
                                        socialViewModel.addUserToFriendsList(currentUser, user.id)
                                    }
                                }
                            }) {
                                Text(
                                    "Adicionar",
                                    color = Color.Black,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
