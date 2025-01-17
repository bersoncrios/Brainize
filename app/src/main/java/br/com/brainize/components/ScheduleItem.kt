package br.com.brainize.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brainize.R
import br.com.brainize.model.Schedule

@Composable
fun ScheduleItem(
    schedule: Schedule,
    onIsDoneChange: (String, Boolean) -> Unit,
    priorityHighColor: Color = Color(0XFF873D48),
    priorityMediumColor: Color = Color(0xFFA6CFD5),
    priorityLowColor: Color =  Color(0xFF90EE90)

) {
    var currentPriority by remember { mutableStateOf(schedule.priority) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(schedule.isDone) }

    val hightPriority = "Alta"
    val mediumPriority = "Média"
    val lowPriority = "Baixa"

    val priorityColor = when (currentPriority) {
        hightPriority -> priorityHighColor
        mediumPriority -> priorityMediumColor
        lowPriority -> priorityLowColor
        else -> Color(0xFFbc60c4)
    }

    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = priorityColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment
                    .CenterVertically
            ) {
                Text(
                    text = schedule.name.uppercase(),
                    fontSize = 20.sp,
                    fontWeight= FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                )
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        showConfirmDialog = true
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.White,
                        uncheckedColor = Color.White,
                        checkmarkColor = Color(0xFF372080)
                    )
                )
            }
            Text(
                text = "Data: ${schedule.date}",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Text(
                text = "Horário: ${schedule.time}",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Text(
                text = "Prioridade:$currentPriority",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Text(
                text = "TAG: ${schedule.tag}",fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
    if (showConfirmDialog) {
        ConfirmActionDialog(
            onConfirm = {
                isChecked = !isChecked
                onIsDoneChange(schedule.id, isChecked)
                showConfirmDialog = false
            },
            onDismiss = {
                showConfirmDialog = false
            },
            schedule = schedule,
            isDone = isChecked
        )
    }
}

@Composable
fun ConfirmActionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    schedule: Schedule,
    isDone: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Concluir a agenda ${schedule.name}?",
                color = Color.White
            )
        },
        text = {
            Text(
                text = "Realmente deseja concluir a agenda ${schedule.name}?",
                color = Color.White
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFbc60c4)
                )
            ) {
                Text(
                    text = stringResource(R.string.confirm_label),
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel_label),
                    color = Color.White
                )
            }
        },
        containerColor = Color(0xFF372080)
    )
}