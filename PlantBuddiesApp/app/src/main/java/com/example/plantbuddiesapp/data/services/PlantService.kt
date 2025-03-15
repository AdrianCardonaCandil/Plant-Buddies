package com.example.plantbuddiesapp.data.services

import com.example.plantbuddiesapp.data.dto.PlantResponseDto
import com.example.plantbuddiesapp.data.dto.SavePlantRequestDto
import com.example.plantbuddiesapp.data.dto.UserPlantDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PlantService {
    @Multipart
    @POST("plants/identify")
    suspend fun identifyPlant(
        @Part image: MultipartBody.Part
    ): Response<PlantResponseDto>

    @GET("plants")
    suspend fun getUserPlants(): Response<List<UserPlantDto>>

    @POST("plants")
    suspend fun savePlant(
        @Body plant: SavePlantRequestDto
    ): Response<UserPlantDto>

    @DELETE("plants/{plantId}")
    suspend fun deletePlant(
        @Path("plantId") plantId: String
    ): Response<Unit>

    @PUT("plants/{plantId}")
    suspend fun updatePlant(
        @Path("plantId") plantId: String,
        @Body plant: SavePlantRequestDto
    ): Response<UserPlantDto>

    @GET("plants/search")
    suspend fun searchPlants(
        @Query("query") query: String,
        @Query("filters") filters: List<String>
    ): Response<List<UserPlantDto>>
}