package br.com.brainize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.brainize.database.AppDatabase
import br.com.brainize.navigation.AppNavigation
import br.com.brainize.ui.theme.BrainizeTheme
import br.com.brainize.viewmodel.CarViewModel
import br.com.brainize.viewmodel.HouseViewModel
import br.com.brainize.viewmodel.factories.CarViewModelFactory
import br.com.brainize.viewmodel.factories.HouseViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val carStatusDao = database.carStatusDao()
        val houseStatusDao = database.houseStatusDao()

        val carViewModel: CarViewModel by viewModels {
            CarViewModelFactory(carStatusDao)
        }
        val houseViewModel: HouseViewModel by viewModels {
            HouseViewModelFactory(houseStatusDao)
        }

        setContent {

            BrainizeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BrainizeApp(
                        carViewModel = carViewModel,
                        houseViewModel = houseViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun BrainizeApp(
    carViewModel: CarViewModel,
    houseViewModel: HouseViewModel
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppNavigation(
                carViewModel = carViewModel,
                houseViewModel = houseViewModel
            )
        }
    }
}