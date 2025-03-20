package com.example.plantbuddiesapp.data.services

import com.example.plantbuddiesapp.data.dto.PlantDto
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
    ): Response<List<PlantDto>>

    @POST("users/plantlist/{plantId}")
    suspend fun savePlant(
        @Header("Authorization") token: String,
        @Path("plantId") plantId: String
    ): Response<PlantDto>

    @DELETE("users/plantlist/{plantId}")
    suspend fun deletePlant(
        @Header("Authorization") token: String,
        @Path("plantId") plantId: String
    ): Response<Unit>

    @GET("plants/")
    suspend fun searchPlants(
        @QueryMap filters: Map<String, String>
    ): Response<List<PlantDto>>

    @GET("plants/{plantId}")
    suspend fun getPlant(
        @Path("plantId") plantId: String
    ): Response<PlantDto>
}