package br.com.brainize.screens.social

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = "Buscar Pessoas",
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
                    label = {
                        Text(
                            text = "Buscar pessoas",
                            color = Color(0xFF372B4B)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .focusRequester(focusRequester),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF372080),
                        unfocusedBorderColor = Color(0xFF372080)
                    ),
                    textStyle = TextStyle(Color(0xFF372B4B))
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(users) { user ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color.Black)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally ,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 16.dp,
                                        bottom = 16.dp,
                                        start = 32.dp,
                                        end = 32.dp
                                    )
                            ) {
                                Column (
                                    modifier = Modifier
                                        .padding(bottom = 16.dp)
                                ) {
                                    Text(
                                        text = user.completeName,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    )
                                    Text(
                                        text = "@${user.username}",
                                        color = Color.Black
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                if (currentUser != null) {
                                                    socialViewModel
                                                        .addUserToFriendsList(currentUser, user.id)
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor =  Color(0xFF372080)
                                        )
                                    ) {
                                        Text(
                                            text = "Adicionar",
                                            color = Color.White,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFF372080))
                                                .clip(RoundedCornerShape(16.dp))
                                                .padding(8.dp)
                                                .wrapContentSize(Alignment.Center)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
