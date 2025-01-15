package br.com.brainize.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import br.com.brainize.R

@Composable
fun BrainizeFloatingActionButton(openDialog: MutableState<Boolean>, title: String) {
    FloatingActionButton(
        onClick = {openDialog.value = true },
        containerColor = Color(0xFF372080),
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = title,
            tint = Color.White
        )
    }
}