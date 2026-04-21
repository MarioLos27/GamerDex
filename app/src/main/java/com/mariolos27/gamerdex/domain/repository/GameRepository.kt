package com.mariolos27.gamerdex.domain.repository

import com.mariolos27.gamerdex.domain.model.Game

/**
 * Contrato del repositorio de juegos.
 * Define las operaciones que se pueden realizar con los datos de juegos.
 * La implementación está en la capa Data.
 */
interface GameRepository {
    /**
     * Busca juegos por query en la API de IGDB
     * @param query Término de búsqueda
     * @return Lista de juegos encontrados
     */
    suspend fun searchGames(query: String): Result<List<Game>>
}

