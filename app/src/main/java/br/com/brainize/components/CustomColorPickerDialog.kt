package br.com.brainize.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker

@Composable
fun ColorPickerComposeDialog(
    showDialog: Boolean,
    initialColor: String,
    title: String,
    onColorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        var selectedColor by remember { mutableStateOf(Color(android.graphics.Color.parseColor(initialColor))) }
        val controller = remember { ColorPickerController() }

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .size(width = 300.dp, height = 400.dp) // Define o tamanho máximo do diálogo
            ) {
                Text(text = title, modifier = Modifier.padding(bottom = 8.dp))
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 10.dp),
                    controller = controller,
                    initialColor = selectedColor,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        selectedColor = colorEnvelope.color
                    }
                )
                AlphaSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    controller = controller,
                )
                BrightnessSlider(
                    modifier = Modifier.fillMaxWidth(),
                    controller = controller,
                )
                Button(
                    onClick = {
                        onColorSelected(String.format("#%06X", (0xFFFFFF and selectedColor.toArgb())))
                        onDismiss()
                    },
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.End)
                        .padding(top = 8.dp)
                ) {
                    Text("Confirmar")
                }
            }
        }
    }
}