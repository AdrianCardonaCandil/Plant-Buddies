package com.example.plantbuddiesapp.data.services

import com.example.plantbuddiesapp.data.dto.PlantDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PlantService {
    /**
     * Identifica una planta a partir de una imagen
     */
    @Multipart
    @POST("plants/identify")
    suspend fun identifyPlant(
        @Part image: MultipartBody.Part
    ): Response<PlantDto>

    /**
     * Obtiene la lista de plantas del usuario
     */
    @GET("plants")
    suspend fun getUserPlants(): Response<List<PlantDto>>

    /**
     * Guarda una nueva planta en la cuenta del usuario
     */
    @POST("plants")
    suspend fun savePlant(
        @Body plant: PlantDto
    ): Response<PlantDto>

    /**
     * Elimina una planta de la cuenta del usuario
     */
    @DELETE("plants/{plantId}")
    suspend fun deletePlant(
        @Path("plantId") plantId: String
    ): Response<Unit>

    /**
     * Actualiza una planta existente
     */
    @PUT("plants/{plantId}")
    suspend fun updatePlant(
        @Path("plantId") plantId: String,
        @Body plant: PlantDto
    ): Response<PlantDto>

    /**
     * Busca plantas según criterios específicos
     */
    @GET("plants/search")
    suspend fun searchPlants(
        @Query("query") query: String,
        @Query("filters") filters: List<String>
    ): Response<List<PlantDto>>
}