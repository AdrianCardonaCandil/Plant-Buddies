package com.example.plantbuddiesapp.data.dto

import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class TaskDto(
    @SerializedName("label") val label: String,
    @SerializedName("type") val type: String,
    @SerializedName("dateTime") val dateTime: String,
    @SerializedName("id") val id: String?
)

enum class TaskType { WATER, PRUNE, HARVEST, UNKNOWN;
    companion object {
        fun fromString(type: String): TaskType {
            return try {
                valueOf(type.uppercase())
            } catch (e: IllegalArgumentException) {
                UNKNOWN
            }
        }
    }
}

fun String.toLocalDateTime(): LocalDateTime {
    return Instant.parse(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}
