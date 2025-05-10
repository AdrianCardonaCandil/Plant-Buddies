package com.example.plantbuddiesapp.data.repository

import android.content.Context
import com.example.plantbuddiesapp.data.mapper.toDomain
import com.example.plantbuddiesapp.data.mapper.toDto
import com.example.plantbuddiesapp.data.services.UserService
import com.example.plantbuddiesapp.domain.model.Task
import com.example.plantbuddiesapp.domain.repository.UserRepository

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val context: Context,
    private val userService: UserService,
    private val tokenManager: TokenManager
) : UserRepository {

    override suspend fun getUserTasks(): Result<List<Task>> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("Not authenticated"))
            val response = userService.getUserTasks(token)
            if (response.isSuccessful) {
                val scheduleResponse = response.body()
                if (scheduleResponse != null) {
                    val tasksDto = scheduleResponse.tasks
                    val tasks = tasksDto.map { taskDto ->
                        taskDto.toDomain()
                    }
                    Result.success(tasks)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                Result.failure(Exception("Failed to get user tasks: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addTask(task: Task): Result<Task> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("Not authenticated"))
            val response = userService.addTask(token, task.toDto())
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.task.toDomain())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to add task: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Task> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("Not authenticated"))
            val response = userService.deleteTask(token, taskId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.task.toDomain())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to delete task: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}