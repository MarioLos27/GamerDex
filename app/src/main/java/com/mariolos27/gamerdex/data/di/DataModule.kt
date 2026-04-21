package com.mariolos27.gamerdex.data.di

import com.mariolos27.gamerdex.data.api.IgdbApi
import com.mariolos27.gamerdex.data.repository.GameRepositoryImpl
import com.mariolos27.gamerdex.domain.repository.GameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Módulo Hilt para la inyección de dependencias de la capa Data.
 *
 * Define cómo construir las instancias de:
 * - Retrofit (HTTP client)
 * - IgdbApi (servicio de API)
 * - GameRepository (repositorio)
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    /**
     * Proporciona una instancia de Retrofit configurada para IGDB.
     *
     * TODO: Añadir interceptor con headers de autenticación (Client-ID y Bearer token)
     * cuando tengas las credenciales de Twitch.
     */
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.igdb.com/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            // TODO: Aquí se añadirá el interceptor con autenticación
            // .addInterceptor(authInterceptor)
            .build()
    }

    /**
     * Proporciona la interfaz IgdbApi usando Retrofit
     */
    @Provides
    @Singleton
    fun provideIgdbApi(retrofit: Retrofit): IgdbApi {
        return retrofit.create(IgdbApi::class.java)
    }

    /**
     * Proporciona la implementación del repositorio de juegos.
     * Aquí es donde se vincula la interfaz con su implementación.
     */
    @Provides
    @Singleton
    fun provideGameRepository(igdbApi: IgdbApi): GameRepository {
        return GameRepositoryImpl(igdbApi)
    }
}

