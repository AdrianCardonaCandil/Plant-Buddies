package com.example.plantbuddiesapp.data.mapper

import com.example.plantbuddiesapp.data.dto.TaskDto
import com.example.plantbuddiesapp.data.dto.TaskType
import com.example.plantbuddiesapp.data.dto.toLocalDateTime
import com.example.plantbuddiesapp.domain.model.Task
import java.time.format.DateTimeFormatter

fun TaskDto.toDomain(): Task {
    return Task(
        dateTime = dateTime.toLocalDateTime(),
        label = label,
        type = TaskType.fromString(type),
        id = id
    )
}

fun Task.toDto(): TaskDto {
    return TaskDto(
        label = label,
        type = type.toString(),
        dateTime = dateTime.format(DateTimeFormatter.ISO_DATE_TIME),
        id = id
    )
}