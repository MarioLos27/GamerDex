package com.mariolos27.gamerdex.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariolos27.gamerdex.domain.usecase.SearchGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de búsqueda de juegos.
 * 
 * Responsabilidades:
 * - Gestionar el estado de la UI usando StateFlow
 * - Ejecutar búsquedas mediante el caso de uso
 * - Manejar corrutinas de forma segura con viewModelScope
 * - Exponer el estado de forma reactiva
 * 
 * Inyectable automáticamente con Hilt.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchGamesUseCase: SearchGamesUseCase
) : ViewModel() {
    
    // Estado privado - Solo el ViewModel puede escribir
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    
    // Estado público - La UI solo puede leer
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    // Campo de búsqueda actual
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    /**
     * Actualiza el query de búsqueda
     */
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }
    
    /**
     * Ejecuta la búsqueda de juegos.
     * Realiza la búsqueda de forma asíncrona y actualiza el estado de la UI.
     */
    fun searchGames() {
        val query = _searchQuery.value
        
        if (query.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }
        
        viewModelScope.launch {
            // Mostrar estado de carga
            _uiState.value = SearchUiState.Loading
            
            try {
                // Llamar al caso de uso (operación suspendible)
                val result = searchGamesUseCase(query)
                
                // Procesar resultado
                result.onSuccess { games ->
                    _uiState.value = SearchUiState.Success(games)
                }.onFailure { exception ->
                    _uiState.value = SearchUiState.Error(
                        message = exception.message ?: "Error desconocido",
                        exception = exception
                    )
                }
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error(
                    message = "Error en la búsqueda: ${e.message}",
                    exception = e
                )
            }
        }
    }
    
    /**
     * Limpia la búsqueda y vuelve al estado inicial
     */
    fun clearSearch() {
        _searchQuery.value = ""
        _uiState.value = SearchUiState.Idle
    }
}

