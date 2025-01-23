package br.com.brainize.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import br.com.brainize.R

@Composable
fun BrainizeScreen(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Color(0xFFF2D2FF)
            ).padding(paddingValues)
    ) {
//        Image(painter = painterResource(id = R.drawable.bg8),
//            contentDescription = null,
//            modifier = modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop,
//            alpha = 1f
//        )
        Surface(
            modifier = modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            content()
        }
    }
}