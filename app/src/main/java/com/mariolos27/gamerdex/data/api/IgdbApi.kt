package com.mariolos27.gamerdex.data.api

import com.mariolos27.gamerdex.data.api.dto.GameDto
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Header

/**
 * Interfaz de Retrofit para la API de IGDB.
 *
 * NOTA: El interceptor con el Bearer token de Twitch se añadirá más adelante
 * mediante un HttpClient interceptor en el módulo de configuración de Retrofit.
 *
 * Para IGDB necesitarás:
 * - Client ID de Twitch
 * - Access Token de Twitch OAuth
 *
 * Estos se enviarán en los headers:
 * - "Client-ID": tu_client_id
 * - "Authorization": "Bearer tu_access_token"
 */
interface IgdbApi {
    /**
     * Endpoint para buscar juegos en IGDB.
     *
     * IGDB usa un sistema de queries personalizado (similar a GraphQL).
     * La URL base será: https://api.igdb.com/v4/
     *
     * @param query Término de búsqueda (ej: "The Witcher")
     * @return Lista de juegos encontrados
     */
    @GET("games")
    suspend fun searchGames(
        @Query("search") query: String
    ): List<GameDto>
}

