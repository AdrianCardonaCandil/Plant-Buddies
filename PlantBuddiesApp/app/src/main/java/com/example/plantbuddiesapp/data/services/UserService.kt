package com.example.plantbuddiesapp.data.services

import com.example.plantbuddiesapp.data.dto.ScheduleResponseDto
import com.example.plantbuddiesapp.data.dto.TaskDto
import com.example.plantbuddiesapp.data.dto.TaskResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


interface UserService {
    @GET("users/tasks")
    suspend fun getUserTasks(
        @Header("Authorization") token: String
    ): Response<ScheduleResponseDto>

    @POST("users/tasks")
    suspend fun addTask(
        @Header("Authorization") token: String,
        @Body taskDto: TaskDto
    ): Response<TaskResponseDto>

    @DELETE("users/tasks/{taskId}")
    suspend fun deleteTask(
        @Header("Authorization") token: String,
        @Path("taskId") taskId: String
    ): Response<TaskResponseDto>
}