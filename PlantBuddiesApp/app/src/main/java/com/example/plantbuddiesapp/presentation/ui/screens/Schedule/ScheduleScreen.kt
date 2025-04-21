package com.example.plantbuddiesapp.presentation.ui.screens.Schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.plantbuddiesapp.presentation.viewmodel.PlantViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ScheduleScreen(
    navController: NavHostController,
    viewModel: PlantViewModel = hiltViewModel()
) {
    var selectedDay by remember { mutableStateOf(Calendar.getInstance()) }
    var weekOffset by remember { mutableStateOf(0) }
    val weekDays = remember(weekOffset) { getWeekDays(weekOffset) }

    var showDialog by remember { mutableStateOf(false) }
    var newTaskText by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(Icons.Default.Opacity) }

    var dialogSelectedDay by remember { mutableStateOf(selectedDay.clone() as Calendar) }

    var currentMonthCalendar by remember { mutableStateOf(Calendar.getInstance()) }

    val tasksMap = remember { mutableStateMapOf<String, MutableList<Pair<String, ImageVector>>>() }

    val selectedDayName = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(selectedDay.time)
    val tasksForSelectedDay = tasksMap.getOrPut(selectedDayName) { mutableListOf() }

    var isListView by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Schedule", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { isListView = !isListView }) {
                    Icon(
                        imageVector = if (isListView) Icons.Default.ViewWeek else Icons.Default.ViewAgenda,
                        contentDescription = if (isListView) "Switch to Calendar View" else "Switch to Week View"
                    )
                }

                if (!isListView) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            currentMonthCalendar.add(Calendar.MONTH, -1)
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Previous month")
                        }

                        Text(
                            SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(currentMonthCalendar.time),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        IconButton(onClick = {
                            currentMonthCalendar.add(Calendar.MONTH, 1)
                        }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Next month")
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { weekOffset-- }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Previous week")
                        }

                        Text(
                            "Week of ${SimpleDateFormat("MMM dd", Locale.ENGLISH).format(weekDays.first().time)}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        IconButton(onClick = { weekOffset++ }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Next week")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isListView) {
                DaySelector(
                    weekDays = weekDays,
                    selectedDay = selectedDay,
                    onDaySelected = { selectedDay = it }
                )
            } else {
                MonthCalendar(
                    selectedDate = selectedDay,
                    monthCalendar = currentMonthCalendar,
                    onDateSelected = { selectedDay = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            DayTasksList(day = selectedDayName, tasks = tasksForSelectedDay)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add Task") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newTaskText,
                            onValueChange = { newTaskText = it },
                            label = { Text("Task name") }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Select a day:", style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        MonthCalendar(
                            selectedDate = dialogSelectedDay,
                            monthCalendar = currentMonthCalendar,
                            onDateSelected = { dialogSelectedDay = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Select an icon:", style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        IconSelector(selectedIcon = selectedIcon) {
                            selectedIcon = it
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newTaskText.isNotBlank()) {
                                val dayName = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(dialogSelectedDay.time)
                                val dayTasks = tasksMap.getOrPut(dayName) { mutableListOf() }
                                dayTasks.add(newTaskText to selectedIcon)

                                newTaskText = ""
                                showDialog = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .shadow(8.dp, CircleShape)
        ) { Icon(Icons.Filled.Add, contentDescription = "Add Task") }
    }
}

@Composable
fun IconSelector(selectedIcon: ImageVector, onIconSelected: (ImageVector) -> Unit) {
    val icons = listOf(
        Icons.Default.Opacity,
        Icons.Default.Build,
        Icons.Default.ShoppingCart,
        Icons.Default.Star
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(icons.size) { index ->
            val icon = icons[index]
            Surface(
                shape = CircleShape,
                color = if (icon == selectedIcon) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onIconSelected(icon) },
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (icon == selectedIcon) MaterialTheme.colorScheme.onPrimary
                           else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun DaySelector(
    weekDays: List<Calendar>,
    selectedDay: Calendar,
    onDaySelected: (Calendar) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDays.forEach { date ->
            val dayName = SimpleDateFormat("EEE", Locale.ENGLISH).format(date.time).uppercase()
            val dayNumber = date.get(Calendar.DAY_OF_MONTH)
            val isSelected = date.get(Calendar.DAY_OF_YEAR) == selectedDay.get(Calendar.DAY_OF_YEAR)

            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface,
                label = "DaySelectorColor"
            )

            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    dayName,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = CircleShape,
                    color = animatedColor,
                    modifier = Modifier
                        .shadow(4.dp, CircleShape)
                        .clickable { onDaySelected(date) }
                ) {
                    Text(
                        text = dayNumber.toString(),
                        modifier = Modifier.padding(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun MonthCalendar(
    selectedDate: Calendar,
    monthCalendar: Calendar,
    onDateSelected: (Calendar) -> Unit
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

                        val isSelected = day.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                                day.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                                day.get(Calendar.DAY_OF_MONTH) == selectedDate.get(Calendar.DAY_OF_MONTH)

                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                                    else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clickable { onDateSelected(day) }
                                .padding(4.dp)
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
fun DayTasksList(day: String, tasks: MutableList<Pair<String, ImageVector>>) {
    val showDeleteDialog = remember { mutableStateOf(false) }
    val taskToDelete = remember { mutableStateOf<Pair<String, ImageVector>?>(null) }

    val formattedDate = try {
        val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(day)
        SimpleDateFormat("EEEE, MMM dd", Locale.ENGLISH).format(parsedDate)
    } catch (e: Exception) {
        day
    }

    Text(formattedDate, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    Spacer(modifier = Modifier.height(8.dp))

    if (tasks.isEmpty()) {
        Text(
            "No tasks today!",
            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic)
        )
    } else {
        tasks.forEach { taskPair ->
            val (task, icon) = taskPair
            val iconColor = when {
                "Water" in task -> Color(0xFF4FC3F7)
                "Prune" in task -> Color(0xFF81C784)
                "Harvest" in task -> Color(0xFFFFB74D)
                else -> MaterialTheme.colorScheme.onSurface
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                    .clickable {
                        taskToDelete.value = taskPair
                        showDeleteDialog.value = true
                    }
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(task, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(onClick = {
                    taskToDelete.value?.let { tasks.remove(it) }
                    showDeleteDialog.value = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text("Cancel")
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