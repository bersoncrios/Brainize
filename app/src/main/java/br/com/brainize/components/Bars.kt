package br.com.brainize.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.com.brainize.R
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrainizerTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    onIconRightClick: () -> Unit,
    rightIcon: ImageVector = Icons.Default.Share,
    hasRightIcon: Boolean = false
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.back_label),
                tint = Color.White
            )
            }
        },
        actions = {
            if (hasRightIcon) {
                IconButton(onClick = onIconRightClick) {
                    Icon(
                        imageVector = rightIcon,
                        contentDescription = "Compartilhar",
                        tint = Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF372080)
        )
    )
}