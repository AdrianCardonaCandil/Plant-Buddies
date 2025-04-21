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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ScheduleScreen(
    navController: NavHostController,
    viewModel: PlantViewModel = hiltViewModel()
) {
    var selectedDay by remember { mutableStateOf(Calendar.getInstance()) }
    var weekOffset by remember { mutableStateOf(0) }
    val weekDays = remember(weekOffset) { getWeekDays(weekOffset) }
    val groupedTasks = sampleGroupedTasks()

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

            Spacer(modifier = Modifier.height(16.dp))
            DaySelector(weekDays = weekDays, selectedDay = selectedDay) { selectedDay = it }
            Spacer(modifier = Modifier.height(16.dp))

            val selectedDayName = SimpleDateFormat("EEEE", Locale.ENGLISH).format(selectedDay.time)
            val tasksForSelectedDay = groupedTasks[selectedDayName] ?: emptyList()

            DayTasksList(day = selectedDayName, tasks = tasksForSelectedDay)
            Divider(modifier = Modifier.padding(vertical = 8.dp))

        }

        FloatingActionButton(
            onClick = { },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .shadow(8.dp, CircleShape)
        ) { Icon(Icons.Filled.Add, contentDescription = "Add Task") }
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
fun DayTasksList(day: String, tasks: List<Pair<String, ImageVector>>) {
    Text(day, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    Spacer(modifier = Modifier.height(8.dp))

    if (tasks.isEmpty()) {
        Text(
            "No tasks today!",
            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic)
        )
    } else {
        tasks.forEach { (task, icon) ->
            val iconColor = when {
                "Water" in task -> Color(0xFF4FC3F7)
                "Prune" in task -> Color(0xFF81C784)
                "Harvest" in task -> Color(0xFFFFB74D)
                else -> MaterialTheme.colorScheme.onSurface
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(task, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

fun sampleGroupedTasks(): Map<String, List<Pair<String, ImageVector>>> {
    return mapOf(
        "Monday" to listOf(
            "Water Basil" to Icons.Default.Opacity,
            "Prune Rosemary" to Icons.Default.Build
        ),
        "Wednesday" to listOf(
            "Water Mint" to Icons.Default.Opacity,
            "Harvest Cherry Tomato" to Icons.Default.ShoppingCart
        ),
        "Friday" to listOf(
            "Water Basil" to Icons.Default.Opacity,
            "Prune Lavender" to Icons.Default.Build
        ),
        "Sunday" to listOf(
            "Water Mint" to Icons.Default.Opacity
        )
    )
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