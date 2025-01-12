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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import br.com.brainize.model.Note

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteItem(
    note: Note,
    onDelete: (String) -> Unit,
    showDialog: (Boolean) -> Unit
) {
    val dismissState = rememberDismissState()
    val isDismissed = dismissState.isDismissed(DismissDirection.EndToStart)
    var showConfirmDialog by remember { mutableStateOf(false) }
    var resetDismissState by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = !isDismissed,
        exit = fadeOut(animationSpec = tween(durationMillis = 600)),
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
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.drop_note_label),
                        tint = Color.White
                    )
                }
            },
            dismissContent = {
                Card(
                    modifier = Modifier.padding(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (note.type == "Tarefa") {
                            Color(0xFF90EE90)
                        } else {
                            Color(0xFFbc60c4)
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(
                                R.string.note_title_text,
                                note.sequentialId,
                                note.title
                            ),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = note.content,
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = stringResource(R.string.note_type_label, note.type),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF372080)
                        )
                        if (note.type == "Tarefa") {
                            if (!note.dueDate.isNullOrEmpty() && !note.dueTime.isNullOrEmpty()) {
                                Text(
                                    text = stringResource(
                                        R.string.label_note_card_task_duedate_duetime,
                                        note.dueDate,
                                        note.dueTime
                                    ),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF372080)
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
            showConfirmDialog = true
            showDialog(true)
        }
    }
    LaunchedEffect(resetDismissState) {
        if (resetDismissState) {
            dismissState.reset()
            resetDismissState = false
        }
    }
    if (showConfirmDialog) {
        ConfirmDeleteDialog(onConfirm = {
            onDelete(note.id)
            showConfirmDialog = false
            showDialog(false)
        },
            onDismiss = {
                showConfirmDialog = false
                showDialog(false)
                resetDismissState = true
            },
            note = note
        )
    }
}

@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    note: Note
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.confirm_delete_note_label, note.sequentialId)) },
        text = { Text(stringResource(R.string.confirm_delete_note_message, note.sequentialId)) },
        confirmButton = {
            TextButton(onClick = onConfirm){
                Text(stringResource(R.string.confirm_label))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_label))
            }
        }
    )
}