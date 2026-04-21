package com.mariolos27.gamerdex.domain.model

/**
 * Modelo de dominio que representa un videojuego.
 * Este modelo es independiente de la fuente de datos (IGDB, Room, etc.)
 */
data class Game(
    val id: Long,
    val title: String,
    val coverUrl: String? = null
)

