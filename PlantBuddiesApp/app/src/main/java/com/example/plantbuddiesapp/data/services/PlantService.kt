package com.example.plantbuddiesapp.data.services

import com.example.plantbuddiesapp.data.dto.PlantDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PlantService {
    @Multipart
    @POST("plants/identify")
    suspend fun identifyPlant(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<PlantDto>

    @GET("plants")
    suspend fun getUserPlants(
        @Header("Authorization") token: String
    ): Response<List<PlantDto>>

    @POST("plants")
    suspend fun savePlant(
        @Header("Authorization") token: String,
        @Body plant: PlantDto
    ): Response<PlantDto>

    @DELETE("plants/{plantId}")
    suspend fun deletePlant(
        @Header("Authorization") token: String,
        @Path("plantId") plantId: String
    ): Response<Unit>

    @PUT("plants/{plantId}")
    suspend fun updatePlant(
        @Header("Authorization") token: String,
        @Path("plantId") plantId: String,
        @Body plant: PlantDto
    ): Response<PlantDto>

    @GET("plants/search")
    suspend fun searchPlants(
        @Header("Authorization") token: String,
        @Query("query") query: String,
        @Query("filters") filters: List<String>
    ): Response<List<PlantDto>>
}