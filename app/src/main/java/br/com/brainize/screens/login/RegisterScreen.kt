package br.com.brainize.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.LoginViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: LoginViewModel) {

    val loginState by viewModel.loginState

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(loginState) {
        if (loginState is LoginViewModel.LoginState.Success) {
            val token = (loginState as LoginViewModel.LoginState.Success).token
            navController.navigate(DestinationScreen.HomeScreen.createRoute(token))
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF372080))
                .padding(paddingValues)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                Text(
                    text = "Crie uma nova conta",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 32.dp),fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { navController.navigate(DestinationScreen.LoginScreen.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                    Text("Tenho uma conta")
                }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.createUserWithEmailAndPassword(email, password) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFbc60c4)
                        )
                    ) {
                        Text(
                            text = "Login",
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFbc60c4))
                                .clip(RoundedCornerShape(16.dp))
                                .padding(8.dp)
                                .wrapContentSize(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (loginState) {
                        is LoginViewModel.LoginState.Loading -> CircularProgressIndicator()
                        is LoginViewModel.LoginState
                        .Error -> Text((loginState as LoginViewModel.LoginState.Error).message)
                        else -> {}
                    }
                }
            }
        }
    }
}
