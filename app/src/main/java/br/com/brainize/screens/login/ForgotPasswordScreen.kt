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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import br.com.brainize.states.LoginState
import br.com.brainize.utils.LoginErrorDisplay
import br.com.brainize.viewmodel.LoginViewModel

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val loginState by viewModel.loginState
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }

    Scaffold { paddingValues ->
        BrainizeScreen(paddingValues = paddingValues) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Image(
                    painter = painterResource(id = R.drawable.brainizelogo),
                    contentDescription = stringResource(R.string.brainize),
                    modifier = Modifier
                        .size(140.dp)
                        .padding(top = 64.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.forgot_password_label),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                BrainizerOutlinedTextField(
                    value = email,
                    label = stringResource(R.string.email_label),
                    icon = Icons.Filled.Email,
                    iconDescription = "Email Icon",
                    placeholder = stringResource(R.string.email_placeholder)
                ) {
                    email = it
                }
                Spacer(modifier = Modifier.height(16.dp))

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
                    Text(stringResource(R.string.back_label))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.recoveryPassword(email, context)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFbc60c4)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.send_label),
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