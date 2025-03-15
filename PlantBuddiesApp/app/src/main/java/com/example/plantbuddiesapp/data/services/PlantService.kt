package com.example.plantbuddiesapp.data.services

import com.example.plantbuddiesapp.data.dto.PlantResponseDto
import com.example.plantbuddiesapp.data.dto.SavePlantRequestDto
import com.example.plantbuddiesapp.data.dto.UserPlantDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PlantService {
    @Multipart
    @POST("identify")
    suspend fun identifyPlant(
        @Part image: MultipartBody.Part
    ): Response<PlantResponseDto>

    @GET("plants")
    suspend fun getUserPlants(): Response<List<UserPlantDto>>

    @POST("plants")
    suspend fun savePlant(
        @Body plant: SavePlantRequestDto
    ): Response<UserPlantDto>
}