package com.example.plantbuddiesapp.domain.repository

import android.net.Uri
import com.example.plantbuddiesapp.domain.model.Task
import com.example.plantbuddiesapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserTasks(): Result<List<Task>>
    suspend fun addTask(task: Task): Result<Task>
    suspend fun deleteTask(taskId: String): Result<Task>
}