package br.com.brainize.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brainize.viewmodel.Schedule

@Composable
fun ScheduleItem(
    schedule: Schedule,
    onDelete: (String) -> Unit,
    onPriorityChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var currentPriority by remember { mutableStateOf(schedule.priority) }

    val priorityColor = when (currentPriority) {
        "Alta" -> Color(0XFF873D48)
        "Média" -> Color(0xFFA6CFD5)
        "Baixa" -> Color(0xFF90EE90)
        else -> Color(0xFFbc60c4)
    }

    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = priorityColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = schedule.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier.align(Alignment.Top)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Excluir Horário",
                        tint = Color.White
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Alta") }, onClick = {
                        currentPriority = "Alta"
                        expanded = false
                    })
                    DropdownMenuItem(text = { Text("Média") }, onClick = {
                        currentPriority = "Média"
                        expanded = false
                    })
                    DropdownMenuItem(text = { Text("Baixa") }, onClick = {
                        currentPriority = "Baixa"
                        expanded = false
                    })
                }
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
                text = "Prioridade: $currentPriority",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
        }
    }
}