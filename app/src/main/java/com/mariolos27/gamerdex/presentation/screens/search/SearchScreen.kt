package com.mariolos27.gamerdex.presentation.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariolos27.gamerdex.presentation.components.ErrorStateContent
import com.mariolos27.gamerdex.presentation.components.GameCard
import com.mariolos27.gamerdex.presentation.components.GamerHeader
import com.mariolos27.gamerdex.presentation.components.GamerSearchField
import com.mariolos27.gamerdex.presentation.components.IdleStateContent
import com.mariolos27.gamerdex.presentation.components.NoResultsStateContent
import com.mariolos27.gamerdex.presentation.theme.DarkBackground

/**
 * Pantalla de búsqueda de videojuegos - VERSIÓN GAMING PERSONALIZADA
 *
 * Características:
 * - Tema oscuro exclusivo con colores vibrantes gamer
 * - Componentes personalizados con gradientes y animaciones
 * - Arquitectura reactiva con StateFlow
 * - 100% Jetpack Compose, sin XML
 * - Inyección de dependencias con Hilt
 */
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    // Observar estados del ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Header con gradient
        GamerHeader()

        // Contenido principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            when (uiState) {
                is SearchUiState.Idle -> {
                    IdleSearchContent(
                        searchQuery = searchQuery,
                        onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        onSearch = { viewModel.searchGames() },
                        onClear = { viewModel.clearSearch() }
                    )
                }

                is SearchUiState.Loading -> {
                    LoadingSearchContent(
                        searchQuery = searchQuery,
                        onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        onSearch = { viewModel.searchGames() },
                        onClear = { viewModel.clearSearch() }
                    )
                }

                is SearchUiState.Success -> {
                    SuccessSearchContent(
                        games = (uiState as SearchUiState.Success).games,
                        searchQuery = searchQuery,
                        onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        onSearch = { viewModel.searchGames() },
                        onClear = { viewModel.clearSearch() }
                    )
                }

                is SearchUiState.Error -> {
                    ErrorSearchContent(
                        message = (uiState as SearchUiState.Error).message,
                        searchQuery = searchQuery,
                        onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        onSearch = { viewModel.searchGames() },
                        onClear = { viewModel.clearSearch() },
                        onRetry = { viewModel.searchGames() }
                    )
                }
            }
        }
    }
}

/**
 * Estado Idle - Sin búsqueda
 */
@Composable
private fun IdleSearchContent(
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Campo de búsqueda
        GamerSearchField(
            query = searchQuery,
            onQueryChanged = onQueryChanged,
            onSearch = onSearch,
            onClear = onClear,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        // Contenido central
        IdleStateContent(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

/**
 * Estado Loading - Búsqueda en progreso
 */
@Composable
private fun LoadingSearchContent(
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Campo de búsqueda
        GamerSearchField(
            query = searchQuery,
            onQueryChanged = onQueryChanged,
            onSearch = onSearch,
            onClear = onClear,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        // Spinner de carga
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

/**
 * Estado Success - Mostrar resultados
 */
@Composable
private fun SuccessSearchContent(
    games: List<com.mariolos27.gamerdex.domain.model.Game>,
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Campo de búsqueda
        GamerSearchField(
            query = searchQuery,
            onQueryChanged = onQueryChanged,
            onSearch = onSearch,
            onClear = onClear,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de resultados
        if (games.isEmpty()) {
            NoResultsStateContent(
                query = searchQuery,
                modifier = Modifier
                    .fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(games) { game ->
                    GameCard(game = game)
                }
            }
        }
    }
}

/**
 * Estado Error - Mostrar error
 */
@Composable
private fun ErrorSearchContent(
    message: String,
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Campo de búsqueda
        GamerSearchField(
            query = searchQuery,
            onQueryChanged = onQueryChanged,
            onSearch = onSearch,
            onClear = onClear,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        // Contenido de error
        ErrorStateContent(
            message = message,
            onRetry = onRetry,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

