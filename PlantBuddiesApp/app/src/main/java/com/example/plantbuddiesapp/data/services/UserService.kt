package com.example.plantbuddiesapp.data.services

import com.example.plantbuddiesapp.data.dto.UserDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface UserService {

    @GET("users/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<UserDto>

    @PUT("users/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body userDto: UserDto
    ): Response<UserDto>

    @Multipart
    @PUT("users/image")
    suspend fun updateProfileImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<UserDto>

    @GET("users/favorites")
    suspend fun getUserFavorites(
        @Header("Authorization") token: String
    ): Response<List<String>>

    @POST("users/favorites/{plantId}")
    suspend fun addToFavorites(
        @Header("Authorization") token: String,
        @Path("plantId") plantId: String
    ): Response<Unit>

    @DELETE("users/favorites/{plantId}")
    suspend fun removeFromFavorites(
        @Header("Authorization") token: String,
        @Path("plantId") plantId: String
    ): Response<Unit>
}