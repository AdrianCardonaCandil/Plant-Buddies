package com.example.plantbuddiesapp.data.dto

data class TaskDto(
    val description: String,
    val taskType: TaskType,
    val date: String,
    val hour: Int,
    val minute : Int
)

enum class TaskType { WATER, PRUNE, HARVEST }
