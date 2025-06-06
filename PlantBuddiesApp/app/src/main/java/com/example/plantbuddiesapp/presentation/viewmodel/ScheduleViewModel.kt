package com.example.plantbuddiesapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantbuddiesapp.data.dto.TaskType
import com.example.plantbuddiesapp.data.services.PlantCareNotificationService
import com.example.plantbuddiesapp.domain.model.Task
import com.example.plantbuddiesapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val plantCareNotificationService: PlantCareNotificationService
): ViewModel() {
    private val _tasks = mutableListOf<Task>()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _tasksForSelectedDate = MutableStateFlow<List<Task>>(emptyList<Task>())
    val tasksForSelectedDate: StateFlow<List<Task>> = _tasksForSelectedDate.asStateFlow()

    private var _nearestTask: Task? = null

    private fun loadAllTasks(tasks: List<Task>) {
        _tasks.clear()
        _tasks.addAll(tasks)
    }

    fun loadUserTasks() {
        viewModelScope.launch {
            userRepository.getUserTasks().fold(
                onSuccess = { tasks ->
                    loadAllTasks(tasks)
                    loadTasksForDate()
                    _nearestTask = tasks.sortedBy { it.dateTime }.firstOrNull { it.dateTime.isAfter(LocalDateTime.now()) }
                    if (_nearestTask != null) {
                        deleteTaskNotification(_nearestTask!!.id!!)
                        scheduleTaskNotification(_nearestTask!!)
                    }
                },
                onFailure = { error ->
                    println("Error loading user tasks: $error")
                }
            )
        }
    }

    private fun loadTasksForDate() {
        val filteredTasks = _tasks.filter { task ->
            task.dateTime.toLocalDate() == _selectedDate.value
        }
        _tasksForSelectedDate.value = filteredTasks.sortedBy { it.dateTime.toLocalTime() }
    }

    fun updateSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        loadTasksForDate()
    }

    fun createTask(label: String, type: String, date: LocalDateTime) {
        val task = Task(label, TaskType.fromString(type), date)
        viewModelScope.launch {
            userRepository.addTask(task)
            loadUserTasks()
        }
    }

    private fun scheduleTaskNotification(task: Task) {
        /*
        println("Scheduling notification for task ${task.id}")
        println("Task type: ${task.type}")
        println("Task label: ${task.label}")
        println("Task date: ${task.dateTime}")
         */
        plantCareNotificationService.scheduleNotification(
            id = task.id!!,
            title = task.type.toString(),
            text = task.label,
            triggerTime = task.dateTime
        )
    }

    private fun deleteTaskNotification(taskId: String) {
        //println("Deleting notification for task $taskId")
        plantCareNotificationService.deleteNotification(taskId)
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            userRepository.deleteTask(taskId)
            loadUserTasks()
        }
    }
}