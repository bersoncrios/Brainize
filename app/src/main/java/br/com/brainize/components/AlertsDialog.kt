package br.com.brainize.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.brainize.R
import br.com.brainize.components.AlertsDialog.Companion.DATE_FORMAT
import br.com.brainize.components.AlertsDialog.Companion.HOUR_FORMAT
import br.com.brainize.components.AlertsDialog.Companion.TASK_LABEL
import br.com.brainize.viewmodel.NotesViewModel
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogNoteType(
    openTypeDialog: MutableState<Boolean>,
    newNoteType: MutableState<String>,
    openDialog: MutableState<Boolean>
){
    AlertDialog(
        onDismissRequest = { openTypeDialog.value = false },
        title = { Text(
            text = stringResource(R.string.note_type_title_alert),
            color =  Color.White
        ) },
        containerColor = Color(0xFF372080),
        tonalElevation = 18.dp,
        text = {
            Column {
                val options = listOf(
                    "Lembrete",
                    "Tarefa"
                )
                val expanded = remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = !expanded.value }
                ) {
                    OutlinedTextField(
                        value = newNoteType.value,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                        options.forEach { option ->
                            DropdownMenuItem(text = { Text(text = option, color = Color.White) },
                                onClick = {
                                    newNoteType.value = option
                                    expanded.value = false
                                })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                openTypeDialog.value = false
                openDialog.value = true
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFbc60c4)
                )
                ) {
                Text(
                    stringResource(id = R.string.continue_label),
                    color =  Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { openTypeDialog.value = false }) {
                Text(
                    stringResource(id = R.string.cancel_label),
                    color =  Color.White
                )
            }
        }
    )
}

@Composable
fun DialogNewNote(
    openDialog: MutableState<Boolean>,
    newNoteTitle: MutableState<String>,
    newNoteContent: MutableState<String>,
    newNoteType: MutableState<String>,
    newNoteTag: MutableState<String>,
    newNoteDueDate: MutableState<String>,
    newNoteDueTime: MutableState<String>,
    viewModel: NotesViewModel,
    context: Context
){
    AlertDialog(
        onDismissRequest = { openDialog.value = false },
        title = { Text(
            text = stringResource(R.string.dialog_new_anotation_title),
            color =  Color.White
        ) },
        containerColor = Color(0xFF372080),
        tonalElevation = 18.dp,
        text = {
            Column {
                OutlinedTextField(
                    value = newNoteTitle.value,
                    onValueChange = { newNoteTitle.value = it },
                    label = {
                        Text(
                            text = stringResource(R.string.dialog_new_anotation_title_label),
                            color =  Color.White
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .height(160.dp),
                    value = newNoteContent.value,
                    onValueChange = { newNoteContent.value = it },
                    label = {
                        Text(
                            text = stringResource(R.string.dialog_new_anotation_what_remember),
                            color =  Color.White
                        )
                    }
                )
                OutlinedTextField(
                    value = newNoteTag.value,
                    onValueChange = { newNoteTag.value = it },
                    label = {
                        Text(
                            text = stringResource(R.string.dialog_new_anotation_tag),
                            color =  Color.White
                        )
                    }
                )
                if (newNoteType.value == TASK_LABEL) {
                    TextButton(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            val datePickerDialog = DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    newNoteDueDate.value = String.format(
                                        Locale.getDefault(),
                                        DATE_FORMAT,
                                        dayOfMonth,
                                        month + 1,
                                        year
                                    )
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            datePickerDialog.show()
                        }
                    ) {
                        Text(
                            text = if (newNoteDueDate.value.isEmpty())
                                stringResource(R.string.define_task_date_label) else
                                stringResource(R.string.done_task_date_label, newNoteDueDate.value),
                            color =  Color.White
                        )
                    }
                    TextButton(onClick = {
                        val calendar = Calendar.getInstance()
                        val timePickerDialog = TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                newNoteDueTime.value = String.format(
                                    Locale.getDefault(),
                                    HOUR_FORMAT,
                                    hourOfDay,
                                    minute
                                )
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        )
                        timePickerDialog.show()
                    }) {
                        Text(
                            text = if (newNoteDueTime.value.isEmpty()) stringResource(R.string.task_hour_label) else
                                stringResource(R.string.define_task_hour_label, newNoteDueTime.value),
                            color =  Color.White
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.createNewNote(
                        title = newNoteTitle.value,
                        content = newNoteContent.value,
                        type = newNoteType.value,
                        tag = newNoteTag.value,
                        dueDate = if (newNoteType.value == TASK_LABEL) newNoteDueDate.value else null,
                        dueTime = if (newNoteType.value == TASK_LABEL) newNoteDueTime.value else null
                    )
                    newNoteTitle.value = ""
                    newNoteContent.value = ""
                    newNoteDueDate.value = ""
                    newNoteDueTime.value = ""
                    openDialog.value = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFbc60c4)
                )
            ) {
                Text(
                    text = stringResource(R.string.save_label),
                    color =  Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog.value = false
                    newNoteDueDate.value = ""
                    newNoteDueTime.value = ""
                }) {
                Text(stringResource(R.string.cancel_label))
            }
        }
    )
}

class AlertsDialog {
    companion object {
        const val TASK_LABEL = "Tarefa"
        const val DATE_FORMAT = "%02d/%02d/%d"
        const val HOUR_FORMAT = "%02d:%02d"
    }
}