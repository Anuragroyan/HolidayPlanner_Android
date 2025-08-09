package com.example.holidayplanner.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.holidayplanner.data.HolidayViewModel
import com.example.holidayplanner.model.Holiday

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HolidayPlannerApp(viewModel: HolidayViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Holiday?>(null) }

    var searchQuery by remember { mutableStateOf("") }

    val holidays by viewModel.holidays.collectAsState()

    // Filter holidays based on search
    val filteredHolidays = holidays.filter { holiday ->
        searchQuery.isBlank() ||
                holiday.title.contains(searchQuery, ignoreCase = true) ||
                holiday.location.contains(searchQuery, ignoreCase = true) ||
                holiday.notes.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp).padding(top= 20.dp)
            ) {
                Text(
                    "Holiday Planner",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.25f),
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                )

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search holidays...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Holiday", tint = Color.White)
            }
        }
    ) { padding ->
        if (filteredHolidays.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (searchQuery.isEmpty()) "No holidays planned yet" else "No holidays match your search",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(filteredHolidays) { holiday ->
                    HolidayCard(
                        holiday = holiday,
                        onEdit = { showEditDialog = holiday },
                        onDelete = { viewModel.deleteHoliday(holiday) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        ShowAddDialog(viewModel = viewModel, onDismiss = { showAddDialog = false })
    }

    showEditDialog?.let { holiday ->
        ShowEditDialog(viewModel = viewModel, holiday = holiday, onDismiss = { showEditDialog = null })
    }
}



@Composable
fun HolidayCard(
    holiday: Holiday,
    onEdit: (Holiday) -> Unit,
    onDelete: (Holiday) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(holiday) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title
            Text(
                holiday.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            // Location (if available)
            if (holiday.location.isNotBlank()) {
                Text(
                    holiday.location,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
            }

            // Notes (if available)
            if (holiday.notes.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    holiday.notes,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Created Date
            holiday.createdAt?.let { timestamp ->
                Spacer(Modifier.height(4.dp))
                val dateText = remember(timestamp) {
                    val date = timestamp.toDate()
                    java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(date)
                }
                Text(
                    "Created: $dateText",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray)
                )
            }

            if (holiday.startDate.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    holiday.startDate,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (holiday.endDate.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    holiday.endDate,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { onEdit(holiday) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { onDelete(holiday) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}


@Composable
fun ShowAddDialog(viewModel: HolidayViewModel, onDismiss: () -> Unit) {
    val ctx = LocalContext.current

    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add Holiday", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") }
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") }
                )
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("startDate") }
                )
                 OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("endDate") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isBlank()) {
                    Toast.makeText(ctx, "Enter title", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.addHoliday(
                        title = title,
                        location = location,
                        notes = notes,
                        startDate = startDate,
                        endDate = endDate
                    )
                    onDismiss()
                }
            }) { Text("Save", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}


@Composable
fun ShowEditDialog(viewModel: HolidayViewModel, holiday: Holiday, onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    var title by remember { mutableStateOf(holiday.title) }
    var location by remember { mutableStateOf(holiday.location) }
    var notes by remember { mutableStateOf(holiday.notes) }
    var startDate by remember { mutableStateOf(holiday.startDate) }
    var endDate by remember { mutableStateOf(holiday.endDate) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit Holiday", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") })
                OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("StartDate") })
                OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("EndDate") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isBlank()) {
                    Toast.makeText(ctx, "Enter title", Toast.LENGTH_SHORT).show()
                } else {
                    val updated = holiday.copy(title = title, location = location, notes = notes, startDate = startDate, endDate = endDate)
                    viewModel.updateHoliday(updated)
                    onDismiss()
                }
            }) { Text("Update", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) { Text("Cancel") }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
