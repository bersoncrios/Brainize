package br.com.brainize.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import androidx.compose.runtime.MutableState
import br.com.brainize.screens.notes.DATE_FORMAT
import br.com.brainize.screens.notes.HOUR_FORMAT
import java.util.Locale

fun showDatePicker(context: Context, newNoteDueDate: MutableState<String>) {
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog( // Use o DatePickerDialog do SDK
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

fun showTimePicker(context: Context, newNoteDueTime: MutableState<String>) {
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
}