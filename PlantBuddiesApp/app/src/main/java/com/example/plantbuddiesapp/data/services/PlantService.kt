package com.example.plantbuddiesapp.data.services

import com.example.plantbuddiesapp.data.dto.FavoritePlantsResponseDto
import com.example.plantbuddiesapp.data.dto.PlantDto
import com.example.plantbuddiesapp.data.dto.PlantListResponseDto
import com.example.plantbuddiesapp.data.dto.PlantResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PlantService {
    @Multipart
    @POST("model")
    suspend fun identifyPlant(
        @Part image: MultipartBody.Part
    ): Response<PlantResponseDto>

    @GET("users/plantlist")
    suspend fun getUserPlants(
        @Header("Authorization") token: String
    ): Response<PlantListResponseDto>

    @POST("users/plantlist/{plantId}")
    suspend fun savePlant(
        @Header("Authorization") token: String,
        @Path("plantId") plantId: String
    ): Response<PlantResponseDto>

    @DELETE("users/plantlist/{plantId}")
    suspend fun deletePlant(
        @Header("Authorization") token: String,
        @Path("plantId") plantId: String
    ): Response<PlantResponseDto>

    @GET("users/favorites")
    suspend fun getUserFavoritePlants(
        @Header("Authorization") token: String
    ): Response<FavoritePlantsResponseDto>

    @POST("users/favorites/{plantId}")
    suspend fun addPlantToFavorites(
        @Header("Authorization") token: String,
        @Path("plantId") plantId: String
    ): Response<PlantResponseDto>

    @DELETE("users/favorites/{plantId}")
    suspend fun removePlantFromFavorites(
        @Header("Authorization") token: String,
        @Path("plantId") plantId: String
    ): Response<PlantResponseDto>

    @POST("plants/")
    suspend fun searchPlants(
        @Body filters: Map<String, String>
    ): Response<PlantListResponseDto>

    @GET("plants/{plantId}")
    suspend fun getPlant(
        @Path("plantId") plantId: String
    ): Response<PlantResponseDto>
}