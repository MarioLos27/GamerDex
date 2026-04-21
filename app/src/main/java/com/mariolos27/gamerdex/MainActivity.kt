package com.mariolos27.gamerdex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.mariolos27.gamerdex.presentation.theme.GamerDexTheme
import com.mariolos27.gamerdex.presentation.screens.search.SearchScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity - Punto de entrada de la aplicación.
 *
 * Configuración:
 * - Edge-to-edge: Usa toda la pantalla incluyendo la barra de estado
 * - Hilt: @AndroidEntryPoint para inyección de dependencias
 * - Compose: Interfaz 100% declarativa sin XML
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GamerDexTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Renderizar la pantalla de búsqueda
                    SearchScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
