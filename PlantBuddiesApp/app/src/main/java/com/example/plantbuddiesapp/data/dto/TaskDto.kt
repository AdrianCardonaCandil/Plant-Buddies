package com.example.plantbuddiesapp.data.dto

data class TaskDto(
    val description: String,
    val taskType: TaskType,
    val date: String
)

enum class TaskType { WATER, PRUNE, HARVEST }
