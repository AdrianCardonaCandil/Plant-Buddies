package com.example.plantbuddiesapp.domain.model

import com.example.plantbuddiesapp.data.dto.TaskType
import java.time.LocalDateTime

data class Task(
    val label: String,
    val type: TaskType,
    val dateTime: LocalDateTime
)