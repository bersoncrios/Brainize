package br.com.brainize.components

import androidx.compose.ui.graphics.Color

fun getColorFromTaskColor(taskColor: String): Color {
    val cleanTaskColor = taskColor.replace("#", "")
    val colorLong = cleanTaskColor.toLong(16)
    return Color(colorLong or 0xFF000000)
}