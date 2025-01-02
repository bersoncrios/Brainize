package br.com.brainize.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BrainizerSelectButton(icon: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .background(
                Color(0xFFbc60c4)
            )
            .padding(30.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "Imagem de exemplo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun BrainizerAlternateSelectButton(
    defaultIcon: Int,
    alternateIcon: Int,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val iconState = rememberUpdatedState(if (isSelected) alternateIcon else defaultIcon)

    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                val newState = iconState.value == defaultIcon
                onToggle(newState)
            }
            .background(Color(0xFFbc60c4))
            .padding(30.dp)
    ) {
        Image(
            painter = painterResource(id = iconState.value),
            contentDescription = "Ícone dinâmico",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}