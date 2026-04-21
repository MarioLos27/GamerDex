package com.mariolos27.gamerdex.data.api.dto

import com.google.gson.annotations.SerializedName
import com.mariolos27.gamerdex.domain.model.Game

/**
 * Data Transfer Object que mapea la respuesta de IGDB.
 * La API de IGDB devuelve la estructura de cobertura de forma anidada.
 */
data class GameDto(
    val id: Long,
    val name: String,
    val cover: CoverDto? = null
) {
    /**
     * Convierte el DTO al modelo de dominio Game
     */
    fun toDomain(): Game {
        return Game(
            id = id,
            title = name,
            coverUrl = cover?.url?.let { "https:$it" } // IGDB devuelve URLs relativas
        )
    }
}

/**
 * DTO para la cobertura del juego
 * Mapea la respuesta anidada de IGDB
 */
data class CoverDto(
    @SerializedName("image_id")
    val imageId: String? = null,
    val url: String? = null
) {
    fun getImageUrl(): String? {
        return imageId?.let { id ->
            // Construcción de URL de Cloudinary (CDN de IGDB)
            "https://images.igdb.com/igdb/image/upload/t_cover_big/$id.jpg"
        }
    }
}

/**
 * Wrapper para la respuesta paginada de IGDB
 */
data class GameSearchResponse(
    val count: Int = 0,
    val results: List<GameDto> = emptyList()
)

