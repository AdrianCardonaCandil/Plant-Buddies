package com.example.plantbuddiesapp.notifications

import java.time.LocalDateTime

data class AlarmRequest(
    val id: String,
    val title: String,
    val content: String,
    val time: LocalDateTime
)
