package com.example.plantbuddiesapp.data.services

import com.example.plantbuddiesapp.data.dto.PlantDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PlantService {
    @Multipart
    @GET("api/model")
    suspend fun identifyPlant(
        @Part image: MultipartBody.Part
    ): Response<PlantDto>

    @GET("api/users/plantlist")
    suspend fun getUserPlants(
        @Header("Authorization") token: String
    ): Response<List<PlantDto>>

    @POST("api/users/plantlist/{plantId}")
    suspend fun savePlant(
        @Header("Authorization") token: String,
        @Path("plantId") plantId: String
    ): Response<PlantDto>

    @DELETE("api/users/plantlist/{plantId}")
    suspend fun deletePlant(
        @Header("Authorization") token: String,
        @Path("plantId") plantId: String
    ): Response<Unit>

    @GET("api/plants/")
    suspend fun searchPlants(
        @Body filters: Set<Any>
    ): Response<List<PlantDto>>

    @GET("api/plants/{plantId}")
    suspend fun getPlant(
        @Path("plantId") plantId: String
    ): Response<PlantDto>
}