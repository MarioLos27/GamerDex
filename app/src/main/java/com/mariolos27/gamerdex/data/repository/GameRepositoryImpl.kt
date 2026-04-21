package com.mariolos27.gamerdex.data.repository

import com.mariolos27.gamerdex.data.api.IgdbApi
import com.mariolos27.gamerdex.domain.model.Game
import com.mariolos27.gamerdex.domain.repository.GameRepository
import javax.inject.Inject

/**
 * Implementación del repositorio de juegos.
 * Coordina la obtención de datos desde IGDB y su transformación al modelo de dominio.
 *
 * Responsabilidades:
 * - Mapear DTOs a modelos de dominio
 * - Manejar errores de la API
 * - Cachear datos si es necesario (futuro: Room)
 */
class GameRepositoryImpl @Inject constructor(
    private val igdbApi: IgdbApi
) : GameRepository {

    override suspend fun searchGames(query: String): Result<List<Game>> {
        return try {
            val response = igdbApi.searchGames(query)
            val games = response.map { it.toDomain() }
            Result.success(games)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

