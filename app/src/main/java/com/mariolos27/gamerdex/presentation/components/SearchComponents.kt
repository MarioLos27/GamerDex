package com.mariolos27.gamerdex.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mariolos27.gamerdex.presentation.theme.AccentGreen
import com.mariolos27.gamerdex.presentation.theme.AccentRed
import com.mariolos27.gamerdex.presentation.theme.DarkBackground
import com.mariolos27.gamerdex.presentation.theme.DarkSurface
import com.mariolos27.gamerdex.presentation.theme.GamerCyan
import com.mariolos27.gamerdex.presentation.theme.GamerPink
import com.mariolos27.gamerdex.presentation.theme.GamerPurple
import com.mariolos27.gamerdex.presentation.theme.GradientEnd
import com.mariolos27.gamerdex.presentation.theme.GradientStart
import com.mariolos27.gamerdex.presentation.theme.TextPrimary
import com.mariolos27.gamerdex.presentation.theme.TextSecondary
import com.mariolos27.gamerdex.presentation.theme.TextTertiary

/**
 * Campo de búsqueda personalizado con tema gaming
 * Incluye animaciones y estilos oscuros vibrantes
 */
@Composable
fun GamerSearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Busca un juego..."
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = GamerPurple.copy(alpha = 0.3f),
                spotColor = GamerPurple.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier
                .fillMaxSize()
                .padding(end = if (query.isNotEmpty()) 8.dp else 0.dp),
            placeholder = {
                Text(
                    placeholder,
                    color = TextTertiary,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = GamerCyan,
                    modifier = Modifier.size(24.dp)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar",
                            tint = GamerPink,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = DarkSurface,
                focusedContainerColor = DarkSurface,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = GamerCyan,
                unfocusedTextColor = TextPrimary,
                focusedTextColor = TextPrimary,
                cursorColor = GamerCyan
            ),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/**
 * Header decorativo con gradiente y título
 */
@Composable
fun GamerHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientEnd),
                    startY = 0f,
                    endY = 200f
                )
            )
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎮 GAMERDEX",
            style = MaterialTheme.typography.displaySmall.copy(
                color = TextPrimary
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Descubre increíbles videojuegos",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary
            ),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Estado Idle con animación
 */
@Composable
fun IdleStateContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(GamerPurple, GamerCyan),
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🔍",
                style = MaterialTheme.typography.displayLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Busca tu juego favorito",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = TextPrimary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Escribe el nombre del juego arriba para comenzar",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary
            ),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Estado de error con estilo gaming
 */
@Composable
fun ErrorStateContent(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = AccentRed.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚠️",
                style = MaterialTheme.typography.displayMedium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Oops, algo salió mal",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = TextPrimary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary
            ),
            textAlign = TextAlign.Center
        )

        if (onRetry != null) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(GamerPink)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onRetry()
                    }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Reintentar",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White
                    )
                )
            }
        }
    }
}

/**
 * Estado No encontrados con mensaje personalizado
 */
@Composable
fun NoResultsStateContent(
    query: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = GamerCyan.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🎲",
                style = MaterialTheme.typography.displayMedium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "No encontramos resultados",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = TextPrimary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Intenta buscar con otro término para \"$query\"",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary
            ),
            textAlign = TextAlign.Center
        )
    }
}

