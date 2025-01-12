package br.com.brainize.screens.login

import BrainizerOutlinedTextField
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.utils.LoginErrorDisplay
import br.com.brainize.viewmodel.LoginViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val loginState by viewModel.loginState
    val context = LocalContext.current
    val usernameExists by viewModel.usernameExists.collectAsState()

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    LaunchedEffect(usernameExists) {
        usernameError = usernameExists
    }

    Scaffold { paddingValues ->
        BrainizeScreen(paddingValues = paddingValues) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Image(
                        painter = painterResource(
                            id = R.drawable.brainizelogo
                        ),
                        contentDescription = stringResource(R.string.brainize),
                        modifier = Modifier
                            .size(140.dp)
                            .padding(top = 64.dp)
                    )

                    Text(
                        text = stringResource(R.string.create_new_account),
                        modifier = Modifier
                            .padding(top = 12.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            end = 32.dp,
                            start = 32.dp,
                            bottom = 32.dp
                        )
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BrainizerOutlinedTextField(
                        value = name,
                        label = stringResource(R.string.your_name_label),
                        iconDescription = stringResource(R.string.has_not_icon),
                        placeholder = stringResource(R.string.name_placeholder)
                    ) {
                        name = it
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    BrainizerOutlinedTextField(
                        value = username,
                        label = stringResource(R.string.username_label),
                        iconDescription = stringResource(R.string.has_not_icon),
                        placeholder = stringResource(R.string.username_placeholder)
                    ) {
                        username = it
                        usernameError = false
                        if (it.isNotBlank()) {
                            viewModel.checkUsernameExists(it)
                        }
                    }
                    if (usernameError) {
                        Text("Este username já está em uso", color = Color.Red)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    BrainizerOutlinedTextField(
                        value = email,
                        label = stringResource(R.string.email_register_label),
                        iconDescription = stringResource(R.string.has_not_icon),
                        placeholder = stringResource(R.string.email_placeholder)
                    ) {
                        email = it
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    BrainizerOutlinedTextField(
                        value = password,
                        label = stringResource(R.string.password_register_label),
                        iconDescription = stringResource(R.string.has_not_icon),
                        placeholder = stringResource(R.string.password_placeholder)
                    ) {
                        password = it
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LoginErrorDisplay(loginState = loginState, context = context)

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { navController.navigate(DestinationScreen.LoginScreen.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            containerColor = Color.Transparent,
                            disabledContentColor = Color.Gray,
                            disabledContainerColor = Color.Transparent,
                        ),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Text(stringResource(R.string.have_account))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (!usernameError) {
                                viewModel.createUserWithEmailAndPassword(
                                    email,
                                    password,
                                    name,
                                    username,
                                    context
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFbc60c4)
                        ),
                        enabled = !usernameError
                    ) {
                        Text(
                            text = stringResource(R.string.enter_label),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFbc60c4))
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