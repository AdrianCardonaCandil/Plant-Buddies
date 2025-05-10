package com.example.plantbuddiesapp.presentation.ui.screens.Schedule

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.plantbuddiesapp.R
import com.example.plantbuddiesapp.domain.model.Task
import com.example.plantbuddiesapp.presentation.viewmodel.ScheduleViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleScreen(navController: NavController, viewModel: ScheduleViewModel = hiltViewModel())
{
    var selectedDay by remember { mutableStateOf(Calendar.getInstance()) }
    var weekOffset by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var taskLabel by remember { mutableStateOf("") }
    var dialogMonthCalendar by remember { mutableStateOf((selectedDay.clone() as Calendar)
        .apply { set(Calendar.DAY_OF_MONTH, 1) }) }

    val context = LocalContext.current
    var selectedHour by remember { mutableStateOf(12) }
    var selectedMinute by remember { mutableStateOf(0) }

    var currentMonthCalendar by remember { mutableStateOf(Calendar.getInstance()) }

    val selectedDate by viewModel.selectedDate.collectAsState()
    val tasksForSelectedDate by viewModel.tasksForSelectedDate.collectAsState()
    var _selectedType: String by remember { mutableStateOf("WATER")}

    val weekDays = remember(weekOffset) { getWeekDays(weekOffset) }
    var isWeekView by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = stringResource(R.string.schedule_title),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { isWeekView = !isWeekView }) {
                    Icon(imageVector = if (isWeekView) Icons.Default.ViewWeek
                    else Icons.Default.ViewAgenda,
                        contentDescription = if (isWeekView) stringResource(R.string.switch_to_calendar)
                        else stringResource(R.string.switch_to_calendar))
                }

                if (!isWeekView) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            val newCalendar = currentMonthCalendar.clone() as Calendar
                            newCalendar.add(Calendar.MONTH, -1)
                            currentMonthCalendar = newCalendar
                        }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.previous_month)
                            )
                        }

                        Text(
                            SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
                                .format(currentMonthCalendar.time),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        IconButton(onClick = {
                            val newCalendar = currentMonthCalendar.clone() as Calendar
                            newCalendar.add(Calendar.MONTH, 1)
                            currentMonthCalendar = newCalendar
                        }) {
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = stringResource(R.string.next_month)
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { weekOffset-- }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.previous_week)
                            )
                        }

                        Text(
                            "Week of ${SimpleDateFormat("MMM dd", Locale.ENGLISH)
                                .format(weekDays.first().time)}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        IconButton(onClick = { weekOffset++ }) {
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = stringResource(R.string.next_week)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isWeekView) {
                DaySelector(
                    weekDays = weekDays, selectedDate = selectedDate,
                    onDaySelected = {
                        viewModel.updateSelectedDate(it.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate())
                    }
                )
            } else {
                MonthCalendar(
                    selectedDate = selectedDate, monthCalendar = currentMonthCalendar,
                    onDateSelected = { viewModel.updateSelectedDate(LocalDate.of(
                        it.get(Calendar.YEAR),
                        it.get(Calendar.MONTH) + 1,
                        it.get(Calendar.DAY_OF_MONTH)
                    ))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            DayTasksList(date = selectedDate, tasks = tasksForSelectedDate, viewModel = viewModel)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }

        if (showDialog) {
            var newTaskDate by remember { mutableStateOf(LocalDate.now()) }
            taskLabel = ""
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = stringResource(R.string.add_task_title)) },
                text = {
                    Column {
                        OutlinedTextField(value = taskLabel, onValueChange = { taskLabel = it },
                            label = { Text(text = stringResource(R.string.add_task_description)) })

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = stringResource(R.string.select_day_description),
                            style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                val newCalendar = dialogMonthCalendar.clone() as Calendar
                                newCalendar.add(Calendar.MONTH, -1)
                                dialogMonthCalendar = newCalendar
                            }) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = stringResource(R.string.previous_month)
                                )
                            }

                            Text(
                                SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
                                    .format(dialogMonthCalendar.time),
                                style = MaterialTheme.typography.bodyLarge
                            )

                            IconButton(onClick = {
                                val newCalendar = dialogMonthCalendar.clone() as Calendar
                                newCalendar.add(Calendar.MONTH, 1)
                                dialogMonthCalendar = newCalendar
                            }) {
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = stringResource(R.string.next_month)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))


                        MonthCalendar(
                            selectedDate = newTaskDate, monthCalendar = dialogMonthCalendar,
                            onDateSelected = { newTaskDate = LocalDate.of(
                                it.get(Calendar.YEAR),
                                it.get(Calendar.MONTH) + 1,
                                it.get(Calendar.DAY_OF_MONTH)
                            )}
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Seleccionar hora:", style = MaterialTheme.typography.labelLarge)

                        val timeText = String.format("%02d:%02d", selectedHour, selectedMinute)
                        Button(onClick = {
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    selectedHour = hour
                                    selectedMinute = minute
                                },
                                selectedHour,
                                selectedMinute,
                                true
                            ).show()
                        }) {
                            Text("Hora seleccionada: $timeText")
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Select an icon:", style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        IconSelector(selectedType = _selectedType) { _selectedType = it }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (taskLabel.isNotBlank()) {
                                val newTaskDateTime = LocalDateTime.of(newTaskDate, LocalTime.of(selectedHour, selectedMinute))
                                viewModel.createTask(taskLabel.trim(), _selectedType, newTaskDateTime)
                                showDialog = false
                            }
                        }
                    ) { Text("Add") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            )
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).shadow(8.dp, CircleShape)
        ) { Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_task_title)) }
    }
}


@Composable
fun IconSelector(selectedType: String, onIconSelected: (String) -> Unit) {
    val icons = listOf(
        Icons.Default.Opacity, Icons.Default.ContentCut,
        Icons.Default.ShoppingBasket, Icons.Default.Star
    )

    val taskTypes = listOf(
        "WATER", "PRUNE", "HARVEST", "UNKNOWN"
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(icons.size) { index ->
            val icon = icons[index]
            Surface(
                shape = CircleShape,
                color = if (index == taskTypes.indexOf(selectedType)) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier.size(48.dp).clickable { onIconSelected(taskTypes[index]) },
            ) {
                Icon(
                    icon, contentDescription = null,
                    tint = if (index == taskTypes.indexOf(selectedType)) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun DaySelector(weekDays: List<Calendar>, selectedDate: LocalDate, onDaySelected: (Calendar) -> Unit)
{
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDays.forEach { date ->
            val dayName = SimpleDateFormat("EEE", Locale.ENGLISH).format(date.time).uppercase()
            val dayNumber = date.get(Calendar.DAY_OF_MONTH)
            val isSelected = LocalDate.of(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH) + 1,
                date.get(Calendar.DAY_OF_MONTH)
            ) == selectedDate

            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface,
                label = "DaySelectorColor"
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    dayName,
                    style = MaterialTheme.typography.labelLarge
                        .copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = CircleShape, color = animatedColor,
                    modifier = Modifier.shadow(4.dp, CircleShape).clickable { onDaySelected(date) }
                ) {
                    Text(
                        text = dayNumber.toString(),
                        modifier = Modifier.padding(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge
                            .copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun MonthCalendar(
    selectedDate: LocalDate, monthCalendar: Calendar, onDateSelected: (Calendar) -> Unit
) {
    val calendar = monthCalendar.clone() as Calendar
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val firstDayOfWeek = if (dayOfWeek == Calendar.SUNDAY) 6
    else dayOfWeek - Calendar.MONDAY

    val totalCells = daysInMonth + firstDayOfWeek
    val weeks = (totalCells + 6) / 7

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach {
                Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }
        for (week in 0 until weeks) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayIndex in 0..6) {
                    val cellIndex = week * 7 + dayIndex
                    val dayNumber = cellIndex - firstDayOfWeek + 1

                    if (cellIndex < firstDayOfWeek || dayNumber > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).padding(8.dp)) {}
                    } else {
                        val day = calendar.clone() as Calendar
                        day.set(Calendar.DAY_OF_MONTH, dayNumber)

                        val isSelected = LocalDate.of(
                            day.get(Calendar.YEAR),
                            day.get(Calendar.MONTH) + 1,
                            day.get(Calendar.DAY_OF_MONTH)
                        ) == selectedDate

                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.Transparent,
                            modifier = Modifier
                                .weight(1f).aspectRatio(1f).padding(4.dp)
                                .clip(CircleShape).clickable { onDateSelected(day) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    dayNumber.toString(),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayTasksList(date: LocalDate, tasks: List<Task>, viewModel: ScheduleViewModel) {
    val formattedDate = remember(date) {
        date.format(DateTimeFormatter.ofPattern("EEEE, MMM dd"))
    }

    Text(formattedDate, style = MaterialTheme.typography.titleMedium
        .copy(fontWeight = FontWeight.Bold))
    Spacer(modifier = Modifier.height(8.dp))

    val iconMap = mapOf(
        "WATER" to Icons.Default.Opacity,
        "PRUNE" to Icons.Default.ContentCut,
        "HARVEST" to Icons.Default.ShoppingBasket,
        "UNKNOWN" to Icons.Default.Star
    )

    val iconColorMap = mapOf(
        "WATER" to Color(0xFF4FC3F7),
        "PRUNE" to Color(0xFF81C784),
        "HARVEST" to Color(0xFFFFB74D),
        "UNKNOWN" to MaterialTheme.colorScheme.onSurface
    )

    if (tasks.isEmpty()) {
        Text(
            text = stringResource(R.string.tasks_empty_description),
            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic)
        )
    } else {
        tasks.forEach { task ->
            var taskToDelete by remember { mutableStateOf<String?>(null) }
            val (label, type, dateTime) = task

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                    .clickable {
                        taskToDelete = task.id
                    }
            ) {
                Icon(iconMap[type.toString()]!! , contentDescription = null, tint = iconColorMap[type.toString()]!!,
                    modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    )
                    Text(
                        dateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }
            }
            DeleteTaskDialog(taskToDelete = taskToDelete, taskLabel = task.label, onDelete = viewModel::deleteTask, onDismiss = { taskToDelete = null })
        }
    }
}

@Composable
fun DeleteTaskDialog(taskToDelete: String?, taskLabel: String, onDelete: (String) -> Unit, onDismiss: () -> Unit) {
    taskToDelete?.let { taskId ->
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(R.string.delete_task_title) + " \"$taskLabel\"") },
            text = { Text(text = stringResource(R.string.delete_task_description)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(taskId)
                    onDismiss()
                }) { Text(text = stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}

fun getWeekDays(offset: Int = 0): List<Calendar> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    calendar.add(Calendar.WEEK_OF_YEAR, offset)

    return List(7) {
        val day = calendar.clone() as Calendar
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        day
    }
}