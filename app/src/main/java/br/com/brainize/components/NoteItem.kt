package br.com.brainize.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brainize.model.Note

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteItem(note: Note, onDelete: (String) -> Unit) {
    val dismissState = rememberDismissState()
    val isDismissed = dismissState.isDismissed(DismissDirection.EndToStart)
    AnimatedVisibility(
        visible = !isDismissed,
        exit = fadeOut(animationSpec = tween(durationMillis = 300)),
    ) {
        SwipeToDismiss(
            state = dismissState,
            directions = setOf(DismissDirection.EndToStart),
            background = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .padding(8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(Icons.Filled.Delete, "Excluir Nota", tint = Color.White)
                }
            },
            dismissContent = {
                Card(
                    modifier = Modifier.padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFbc60c4))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "#${note.sequentialId} - ${note.title}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = note.content,
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                        Text(text = "Tipo: ${note.type}",
                            fontSize = 14.sp,
                            color =  Color(0xFF372080)
                        )
                        if (note.type == "Tarefa") {
                            if (!note.dueDate.isNullOrEmpty() && !note.dueTime.isNullOrEmpty()) {
                                Text(
                                    text = "Conclusão: ${note.dueDate} ${note.dueTime}",
                                    fontSize = 14.sp,
                                    color =  Color(0xFF372080)
                                )
                            }
                        }
                    }
                }
            }
        )
    }
    LaunchedEffect(isDismissed) {
        if (isDismissed) {
            onDelete(note.id)
        }
    }
    LaunchedEffect(Unit){
        if(isDismissed){
            dismissState.reset()
        }
    }
}