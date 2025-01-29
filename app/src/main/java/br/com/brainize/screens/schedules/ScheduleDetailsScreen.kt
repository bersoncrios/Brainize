package br.com.brainize.screens.schedules

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.brainize.R
import br.com.brainize.components.BrainizeFloatingActionButton
import br.com.brainize.components.BrainizeScreen
import br.com.brainize.components.BrainizerTopAppBar
import br.com.brainize.components.EditDetailItemOnNotesBottomSheet
import br.com.brainize.components.EditDueDateBottomSheet
import br.com.brainize.components.EditDueTimeBottomSheet
import br.com.brainize.components.NewScheduleBottomSheet
import br.com.brainize.components.ScheduleItem
import br.com.brainize.components.getColorFromTaskColor
import br.com.brainize.navigation.DestinationScreen
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.NotesViewModel
import br.com.brainize.viewmodel.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ScheduleDetailsScreen(
    navController: NavController,
    viewModel: ScheduleViewModel,
    loginViewModel: LoginViewModel,
    configurationsViewModel: ConfigurationsViewModel,
    token: String?,
    scheduleId: String?
) {
    val scheduleState by viewModel.scheduleState.collectAsState()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var dueTime by remember {mutableStateOf("") }
    val scrollState = rememberScrollState()
    var isLoading by remember { mutableStateOf(true) }
    var openTitleDialog = remember { mutableStateOf(false) }
    var openContentDialog = remember { mutableStateOf(false) }
    var openDueDateDialog = remember { mutableStateOf(false) }
    var openDueTimeDialog = remember { mutableStateOf(false) }

    val daysUntilSchedule = viewModel.getDaysUntilSchedule(scheduleState.date)

    val dayUntil = when (daysUntilSchedule) {
        0 -> "Compromisso acontecerá em breve"
        in Int.MIN_VALUE..-1 -> "Compromisso foi há ${-daysUntilSchedule} dias"
        else -> "Compromisso em $daysUntilSchedule dias"
    }

    val context = LocalContext.current

    if (!loginViewModel.hasLoggedUser() && token.isNullOrEmpty()) {
        navController.navigate(DestinationScreen.LoginScreen.route)
    }

    LaunchedEffect(scheduleId) {
        isLoading = true
        if (!scheduleId.isNullOrEmpty()) {
            viewModel.getScheduleById(scheduleId)
        }
        configurationsViewModel.loadConfigurations {
            isLoading = false
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            BrainizerTopAppBar(
                title = "Detalhes da agenda",
                onBackClick = {
                    navController.popBackStack()
                },
                onIconRightClick = {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Compartilhando uma agenda do Brainize \uD83E\uDDE0:" +
                                    "\n\n" +
                                    scheduleState.name +
                                    "\n\n\n" +
                                    SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR")).format(scheduleState.date) +
                                    " às " +
                                    scheduleState.time
                        )
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                },
                hasRightIcon = true
            )
        }
    ) { paddingValues ->
        BrainizeScreen(paddingValues = paddingValues) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    scheduleState.let { schedule ->
                        val formattedDate = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR")).format(schedule.date)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(
                                text = scheduleState.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF372080),
                                modifier = Modifier
                                    .padding(bottom = 16.dp)
                            )
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(vertical = 8.dp)
                                .verticalScroll(scrollState),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when (schedule.priority) {
                                    "Baixa" -> Color(getColorFromTaskColor(configurationsViewModel.priorityLowColor).value)
                                    "Média" -> Color(getColorFromTaskColor(configurationsViewModel.priorityMediumColor).value)
                                    "Alta" -> Color(getColorFromTaskColor(configurationsViewModel.priorityHighColor).value)
                                    else -> Color(getColorFromTaskColor(configurationsViewModel.reminderColor).value)
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)

                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "TAG: ${schedule.tag}",
                                        color = Color(0xFF372B4B),
                                        modifier = Modifier.weight(2f),
                                        fontWeight = FontWeight.ExtraLight,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))


                                Text(
                                    text = "Agenda marcada para o dia $formattedDate às ${schedule.time}",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 21.sp
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                Text(
                                    text = "$dayUntil",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}