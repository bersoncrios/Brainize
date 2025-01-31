package br.com.brainize.components

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.brainize.R
import br.com.brainize.components.BrainizeBottonSheets.Companion.TASK_LABEL
import br.com.brainize.model.Note
import br.com.brainize.utils.showDatePicker
import br.com.brainize.utils.showDatePickerForDate
import br.com.brainize.utils.showTimePicker
import br.com.brainize.viewmodel.NotesViewModel
import br.com.brainize.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetNoteType(
    openBottomSheet: MutableState<Boolean>,
    newNoteType: MutableState<MutableState<String>>,
    onConfirm: () -> Unit
) {

    val scaffoldState = rememberBottomSheetScaffoldState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    if (openBottomSheet.value) {
        LaunchedEffect(openBottomSheet.value) {
            coroutineScope.launch {
                scaffoldState.bottomSheetState.expand()
                sheetState.show()
                sheetState.expand()
            }
        }

        BottomSheetScaffold(
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .heightIn(min = 128.dp)
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.note_type_title_alert),
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )
                    val options = listOf(
                        "Lembrete",
                        "Tarefa"
                    )
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = newNoteType.value.value,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White
                            ),
                            textStyle = TextStyle(color = Color.White)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            containerColor = Color(0xFFbc60c4)
                        ) {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(text = option, color = Color.White) },
                                    onClick = {
                                        newNoteType.value.value = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { openBottomSheet.value = false }) {
                            Text(stringResource(id = R.string.cancel_label), color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                openBottomSheet.value = false
                                onConfirm()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFbc60c4))
                        ) {
                            Text(stringResource(id = R.string.continue_label), color = Color.White)
                        }
                    }
                }
            },
            scaffoldState = scaffoldState,
            sheetContainerColor = Color(0xFF372080)
        ) {}
    }
}

// Função para o BottomSheet de nova nota
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetNewNote(
    openBottomSheet: MutableState<Boolean>,
    newNoteTitle: MutableState<String>,
    newNoteContent: MutableState<String>,
    newNoteType: MutableState<String>,
    newNoteTag: MutableState<String>,
    newNoteDueDate: MutableState<String>,
    newNoteDueTime: MutableState<String>,
    viewModel: NotesViewModel,
    context: Context,
    onConfirm: () -> Unit
) {

    val scaffoldState = rememberBottomSheetScaffoldState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    if (openBottomSheet.value) {
        LaunchedEffect(openBottomSheet.value) {
            coroutineScope.launch {
                scaffoldState.bottomSheetState.expand()
                sheetState.show()
                sheetState.expand()
            }
        }

        BottomSheetScaffold(
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .wrapContentHeight()
                        .heightIn(min = 256.dp)

                ) {
                    OutlinedTextField(
                        value = newNoteTitle.value,
                        onValueChange = { newNoteTitle.value = it },
                        label = { Text(text = stringResource(R.string.dialog_new_anotation_title_label), color = Color.White) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        ),
                        textStyle = TextStyle(color = Color.White)
                    )

                    OutlinedTextField(
                        value = newNoteContent.value,
                        onValueChange = { newNoteContent.value = it },
                        label = { Text(text = stringResource(R.string.dialog_new_anotation_what_remember), color = Color.White) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        ),
                        textStyle = TextStyle(color = Color.White)
                    )

                    OutlinedTextField(
                        value = newNoteTag.value,
                        onValueChange = { newNoteTag.value = it },
                        label = { Text(text = stringResource(R.string.dialog_new_anotation_tag), color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        ),
                        textStyle = TextStyle(color = Color.White)
                    )

                    if (newNoteType.value == TASK_LABEL) {
                        TextButton(
                            onClick = { showDatePicker(context, newNoteDueDate) }
                        ) {
                            Text(
                                text = if (newNoteDueDate.value.isEmpty()) stringResource(R.string.define_task_date_label) else stringResource(
                                    R.string.done_task_date_label,
                                    newNoteDueDate.value
                                ),
                                color = Color.White
                            )
                        }
                        TextButton(
                            onClick = { showTimePicker(context, newNoteDueTime) }
                        ) {
                            Text(
                                text = if (newNoteDueTime.value.isEmpty()) stringResource(R.string.task_hour_label) else stringResource(
                                    R.string.define_task_hour_label,
                                    newNoteDueTime.value
                                ),
                                color = Color.White
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { openBottomSheet.value = false }) {
                            Text(stringResource(R.string.cancel_label), color = Color.White)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                viewModel.createNewNote(
                                    title = newNoteTitle.value,
                                    content = newNoteContent.value,
                                    type = newNoteType.value,
                                    tag = newNoteTag.value,
                                    dueDate = if (newNoteType.value == br.com.brainize.screens.notes.TASK_LABEL) newNoteDueDate.value else null,
                                    dueTime = if (newNoteType.value == br.com.brainize.screens.notes.TASK_LABEL) newNoteDueTime.value else null
                                )
                                newNoteTitle.value = ""
                                newNoteContent.value = ""
                                newNoteDueDate.value = ""
                                newNoteDueTime.value = ""
                                openBottomSheet.value = false
                                onConfirm()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFbc60c4))
                        ) {
                            Text(stringResource(R.string.save_label), color = Color.White)
                        }
                    }
                }
            },
            scaffoldState = scaffoldState,
            sheetContainerColor = Color(0xFF372080)
        ) {}
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewScheduleBottomSheet(
    openBottomSheet: MutableState<Boolean>,
    context: Context,
    viewModel: ScheduleViewModel
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val newScheduleName = remember { mutableStateOf("") }
    val newScheduleTag = remember { mutableStateOf("") }
    val newScheduleDate = remember { mutableStateOf<Date>(Date()) }
    val newScheduleTime = remember { mutableStateOf("") }
    var newSchedulePriority by remember { mutableStateOf("") }
    var expandedPriority by remember { mutableStateOf(false) }

    if (openBottomSheet.value) {
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                scaffoldState.bottomSheetState.expand()
            }
        }

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp)) {
                    Text(
                        text = "Nova agenda",
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )

                    // OutlinedTextFields
                    OutlinedTextField(
                        value = newScheduleName.value,
                        onValueChange = { newScheduleName.value = it },
                        label = { Text("Nome", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        ),
                        textStyle = TextStyle(color = Color.White)
                    )
                    OutlinedTextField(
                        value = newScheduleTag.value,
                        onValueChange = { newScheduleTag.value = it },
                        label = { Text("Tag", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        ),
                        textStyle = TextStyle(color = Color.White)
                    )
                    OutlinedTextField(
                        value = if (newScheduleDate.value != null) dateFormatter.format(newScheduleDate.value) else "",
                        onValueChange = { },
                        modifier = Modifier
                            .clickable { showDatePickerForDate(context, newScheduleDate) }
                            .fillMaxWidth(),
                        label = { Text("Data", color = Color.White) },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                contentDescription = "Selecionar Data",
                                tint = Color.White,
                                modifier = Modifier.clickable { showDatePickerForDate(context, newScheduleDate) }
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        ),
                        textStyle = TextStyle(color = Color.White)
                    )
                    OutlinedTextField(
                        value = newScheduleTime.value,
                        onValueChange = { },
                        label = { Text("Horário", color = Color.White) },
                        readOnly = true,
                        modifier = Modifier
                            .clickable { showTimePicker(context, newScheduleTime) }
                            .fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_access_time_24),
                                contentDescription = "Selecionar Horário",
                                tint = Color.White,
                                modifier = Modifier.clickable { showTimePicker(context, newScheduleTime) }
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        ),
                        textStyle = TextStyle(color = Color.White)
                    )
                    OutlinedTextField(
                        value = newSchedulePriority,
                        onValueChange = { },
                        label = { Text("Prioridade", color = Color.White) },
                        readOnly = true,
                        modifier= Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                                contentDescription = "Selecionar Prioridade",
                                modifier = Modifier.clickable { expandedPriority = true },
                                tint = Color.White
                            )
                            DropdownMenu(
                                expanded = expandedPriority,
                                onDismissRequest = { expandedPriority = false },
                                containerColor = Color(0xFFbc60c4),
                                modifier = Modifier
                                    .fillMaxWidth(.92f)
                                    .padding(horizontal = 16.dp)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Alta",
                                            color = Color.White
                                        )
                                    },
                                    onClick = {
                                        newSchedulePriority = "Alta"
                                        expandedPriority = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Média",
                                            color = Color.White)
                                    },
                                    onClick = {
                                        newSchedulePriority = "Média"
                                        expandedPriority = false
                                    })
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Baixa",
                                            color = Color.White
                                        )
                                    },
                                    onClick = {
                                        newSchedulePriority = "Baixa"
                                        expandedPriority = false
                                    })
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        ),
                        textStyle = TextStyle(color = Color.White)
                    )

                    // Botões Salvar e Cancelar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { openBottomSheet.value = false }) {
                            Text("Cancelar", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.createNewSchedule(
                                    newScheduleTime.value,
                                    newScheduleDate.value,
                                    newScheduleName.value,
                                    newSchedulePriority,
                                    newScheduleTag.value,
                                    false
                                )
                                newScheduleTime.value = ""
                                newScheduleDate.value = Date()
                                newScheduleName.value = ""
                                newSchedulePriority = ""
                                newScheduleTag.value = ""
                                openBottomSheet.value = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFbc60c4))
                        ) {
                            Text("Salvar", color = Color.White)
                        }
                    }
                }
            },
            sheetContainerColor = Color(0xFF372080)
        ) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDetailItemOnNotesBottomSheet(
    openBottomSheet: MutableState<Boolean>,
    viewModel: NotesViewModel,
    item:  MutableState<String>,
    fieldName: String,
    label: String = "",
    hint: String = "",
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val item = item
    val noteState by viewModel.noteState.collectAsState()

    if (openBottomSheet.value) {
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                scaffoldState.bottomSheetState.expand()
            }
        }

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text= label,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )

                    OutlinedTextField(
                        value = item.value,
                        onValueChange = { item.value = it },
                        label = { Text(hint, color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        ),
                        textStyle = TextStyle(color = Color.White)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { openBottomSheet.value = false }
                        ) {
                            Text("Cancelar", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                noteState.let { note ->
                                    val updatedNote = updateNoteField(note, fieldName, item.value)
                                    viewModel.updateNote(updatedNote)
                                }
                                openBottomSheet.value = false
                            },
                            colors = ButtonDefaults
                                .buttonColors(
                                    containerColor = Color(0xFFbc60c4)
                                )
                        ) {
                            Text("Salvar", color = Color.White)
                        }
                    }
                }
            },
            sheetContainerColor = Color(0xFF372080)
        ) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDueDateBottomSheet(
    openBottomSheet: MutableState<Boolean>,
    viewModel: NotesViewModel,
    context: Context,
    noteState: Note,
    newDuedate: MutableState<String>
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()


    if (openBottomSheet.value) {
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                scaffoldState.bottomSheetState.expand()
            }
        }

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Editar Data Final",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = { showDatePicker(context, newDuedate) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text(text = newDuedate.value.ifBlank { "Selecionar Data" }, color = Color.White)}

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { openBottomSheet.value = false }) {
                            Text("Cancelar", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                noteState.let { note ->
                                    val updatedNote = note.copy(dueDate = if (newDuedate.value.isNotBlank()) newDuedate.value else null)
                                    viewModel.updateNote(updatedNote)
                                }
                                openBottomSheet.value = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFbc60c4))
                        ) {
                            Text("Salvar", color = Color.White)
                        }
                    }
                }
            },
            sheetContainerColor = Color(0xFF372080)
        ) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDueTimeBottomSheet(
    openBottomSheet: MutableState<Boolean>,
    viewModel: NotesViewModel,
    context: Context,
    noteState: Note,
    newDueTime: MutableState<String>
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    if (openBottomSheet.value) {
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                scaffoldState.bottomSheetState.expand()
            }
        }

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Editar Hora Final",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = { showTimePicker(context, newDueTime) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text(text = newDueTime.value.ifBlank { "SelecionarHora" }, color = Color.White)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { openBottomSheet.value = false }) {
                            Text("Cancelar", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                noteState.let { note ->
                                    val updatedNote = note.copy(dueTime = if (newDueTime.value.isNotBlank()) newDueTime.value else null)
                                    viewModel.updateNote(updatedNote)
                                }
                                openBottomSheet.value = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFbc60c4))
                        ) {
                            Text("Salvar", color = Color.White)
                        }
                    }
                }
            },
            sheetContainerColor = Color(0xFF372080)
        ) {}
    }
}

private fun updateNoteField(note: Note, fieldName: String, fieldValue: String): Note {
    val noteClass = note::class.java
    val field = noteClass.getDeclaredField(fieldName)
    field.isAccessible = true
    field.set(note, fieldValue)
    return note
}

class BrainizeBottonSheets {
    companion object {
        const val TASK_LABEL = "Tarefa"
        const val DATE_FORMAT = "%02d/%02d/%d"
        const val HOUR_FORMAT = "%02d:%02d"
    }
}