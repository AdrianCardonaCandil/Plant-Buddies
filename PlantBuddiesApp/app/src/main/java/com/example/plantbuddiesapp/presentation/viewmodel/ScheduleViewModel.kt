package com.example.plantbuddiesapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantbuddiesapp.data.dto.TaskType
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
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val userRepository: UserRepository,
): ViewModel() {
    private val _tasks = mutableListOf<Task>()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _tasksForSelectedDate = MutableStateFlow<List<Task>>(emptyList<Task>())
    val tasksForSelectedDate: StateFlow<List<Task>> = _tasksForSelectedDate.asStateFlow()

    private fun loadAllTasks(tasks: List<Task>) {
        _tasks.clear()
        _tasks.addAll(tasks)
    }

    init {
        loadUserTasks()
    }

    private fun loadUserTasks() {
        viewModelScope.launch {
            userRepository.getUserTasks().fold(
                onSuccess = { tasks ->
                    if (tasks.isNotEmpty()) {
                        println("Cargando tareas del usuario")
                        for (task in tasks) {
                            val date = task.dateTime
                            val day = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            println("Tarea para $day: ${task.label}")
                        }
                    }
                    println("Tareas del usuario cargadas")
                    loadAllTasks(tasks)
                    loadTasksForDate()
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

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            userRepository.deleteTask(taskId)
            loadUserTasks()
        }
    }
}