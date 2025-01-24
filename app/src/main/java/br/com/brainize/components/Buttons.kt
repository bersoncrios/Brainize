package br.com.brainize.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BrainizerSelectButton(icon: Int, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, // Remove a ondulação padrão
                    onClick = {
                        onClick()
                        isPressed = !isPressed // Inverte o estado de pressionado
                    }
                )
                .background(
                    if (isPressed) {
                        Color(0xFFbc60c4) // Cor quando pressionado
                    } else {Color(0xFF372080) // Cor normal
                    }
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

    // LaunchedEffect para controlar a cor de fundo
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPressed = true
                is PressInteraction.Release, is PressInteraction.Cancel -> isPressed = false
            }
        }
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
            .background(Color(0xFF372080))
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