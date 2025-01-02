package br.com.brainize.screens.house

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizerAlternateSelectButton
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.viewmodel.HouseViewModel

@Composable
fun HouseScreen(navController: NavController, viewModel: HouseViewModel) {

    LaunchedEffect(Unit) {
        try {
            viewModel.loadStatus()
        } catch (e: Exception) {
            Log.e("HouseScreen", "Error loading status", e)
        }
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = "Minha Casa",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0xFF372080)
                )
                .padding(paddingValues)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        BrainizerAlternateSelectButton(
                            defaultIcon = R.drawable.openedwindow,
                            alternateIcon = R.drawable.closedwindow,
                            isSelected = viewModel.windowClosed,
                            onToggle = { selected ->
                                viewModel.windowClosed = selected
                                viewModel.saveStatus()
                            }
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        BrainizerAlternateSelectButton(
                            defaultIcon = R.drawable.opened,
                            alternateIcon = R.drawable.closed,
                            isSelected = viewModel.doorClosed,
                            onToggle = { selected ->
                                viewModel.doorClosed = selected
                                viewModel.saveStatus()
                            }
                        )
                    }
                }
            }
        }
    }
}
