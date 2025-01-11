import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp


@Composable
fun BrainizerOutlinedTextField(
    value: String,
    label: String,
    isPassword: Boolean = false,
    icon: ImageVector,
    iconDescription: String,
    placeholder: String,
    onLabelChange: (String) -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = onLabelChange,
        label = { Text(label) },
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color.White,
            focusedBorderColor = Color(0xFFbc60c4),
            unfocusedBorderColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White,
            cursorColor = Color(0xFFbc60c4)
        ),
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                tint = Color.White
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                color = Color.White
            )
        },
        shape = RoundedCornerShape(8.dp)
    )
}