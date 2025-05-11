package com.example.plantbuddiesapp.notifications

interface AlarmScheduler {
    fun schedule(request: AlarmRequest)
    fun cancel(requestId: String)
}