package com.el_aouthmanie.nticapp.ui.screens.anouncmentsScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.el_aouthmanie.nticapp.globals.CONSTANTS
import com.el_aouthmanie.nticapp.modules.intities.Notification
import com.el_aouthmanie.nticapp.modules.intities.Seance
import com.el_aouthmanie.nticapp.modules.realmHandler.RealmManager
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val animatedScale = animateDpAsState(
        targetValue = if (searchText.isNotEmpty()) 48.dp else 56.dp,
        animationSpec = tween(durationMillis = 300)
    )
    val realm = RealmManager.realm
    val notifications = remember {
        realm.query<Notification>().find().toList()
    }

    var selectedType by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("") }
    var showUnreadOnly by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Notifications", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(CONSTANTS.Screens.HOME){
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    } }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .scale((animatedScale.value / 56f).value),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                placeholder = { Text("Search notifications...") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Notifications List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    notifications.reversed().filter { notification ->
                        (notification.title.contains(searchText, ignoreCase = true)) &&
                                (selectedType.isEmpty() || notification.type == selectedType) &&
                                (selectedPriority.isEmpty() || notification.priority == selectedPriority) &&
                                (!showUnreadOnly || !notification.isRead)
                    }
                ) { notification ->
                    NotificationItem(notification = notification)
                }
            }
        }
    }

    // Filter Dialog
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter Notifications") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Type")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val info = "INFO"
                        val warning = "WARNING"

                        FilterChip(
                            selected = selectedType == info,
                            onClick = { selectedType = if (selectedType == info) "" else info },
                            label = { Text("Info") }
                        )
                        FilterChip(
                            selected = selectedType == "Scheduile Update",
                            onClick = { selectedType = if (selectedType == warning) "" else warning },
                            label = { Text("Warning") }
                        )

                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Priority")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedPriority == "LOW",
                            onClick = { selectedPriority = if (selectedPriority == "LOW") "" else "LOW" },
                            label = { Text("Low") }
                        )
                        FilterChip(
                            selected = selectedPriority == "NORMAL",
                            onClick = { selectedPriority = if (selectedPriority == "NORMAL") "" else "NORMAL" },
                            label = { Text("Normal") }
                        )
                        FilterChip(
                            selected = selectedPriority == "HIGH",
                            onClick = { selectedPriority = if (selectedPriority == "HIGH") "" else "HIGH" },
                            label = { Text("High") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = showUnreadOnly,
                            onCheckedChange = { showUnreadOnly = it }
                        )
                        Text("Show Unread Only")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationItem(notification: Notification) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Small Icon
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Calendar Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sender: ${notification.sender}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Date
            val formattedDate = try {
                val parsedDate = ZonedDateTime.parse(notification.createdAt) // or use LocalDateTime if no zone

                val formatter = DateTimeFormatter.ofPattern("EEE d MMM yyyy, HH:mm", Locale.FRENCH)
                parsedDate.format(formatter).replaceFirstChar { it.uppercase() } // Capitalize first char
            } catch (e: DateTimeParseException) {
                "Date invalide"
            }

            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Top)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAnnouncementsScreen() {
    val navController = rememberNavController()
    AnnouncementsScreen(navController = navController)
}
