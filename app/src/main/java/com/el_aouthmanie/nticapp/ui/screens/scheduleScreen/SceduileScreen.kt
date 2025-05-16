package com.el_aouthmanie.nticapp.ui.screens.scheduleScreen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.el_aouthmanie.nticapp.modules.OnlineDataBase
import com.el_aouthmanie.nticapp.modules.intities.ClassBundle
import com.el_aouthmanie.nticapp.modules.intities.Seance
import com.el_aouthmanie.nticapp.modules.realmHandler.RealmManager
import com.el_aouthmanie.nticapp.ui.compenents.TitleAppBar
import com.el_aouthmanie.nticapp.ui.screens.homeScreen.mod
import com.el_aouthmanie.nticapp.ui.screens.scheduleScreen.components.ClassList
import com.el_aouthmanie.nticapp.ui.screens.scheduleScreen.components.HeaderSection
import io.realm.kotlin.ext.query
import kotlinx.coroutines.launch
val dayIndex = mapOf(
    "Lundi" to "MON",
    "Mardi" to "TUE",
    "Mercredi" to "WED",
    "Jeudi" to "THU",
    "Vendredi" to "FRI",
    "Samedi" to "SAT",
)

@Composable
fun ScheduleScreen() {
    val onlineDataBase = OnlineDataBase
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 6 }
    var updateTrigger by rememberSaveable { mutableStateOf(true) }
    var isItUpToDate by rememberSaveable { mutableStateOf(false) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var newGroup by rememberSaveable { mutableStateOf("") }

    val classBundles = mutableSetOf<ClassBundle>()
    var finishedLoading by rememberSaveable { mutableStateOf(false) }
    val ctx = LocalContext.current
    var grp by rememberSaveable { mutableStateOf(OnlineDataBase.getGroup(ctx)) }
    val realm = RealmManager.realm

    LaunchedEffect(updateTrigger,grp) {
        scope.launch {
            onlineDataBase.syncClasses(
                grp,
                mod,
                realm,
                scope,
                onFailureResponse = {
                    val courseData = realm.query<Seance>().find()

                    if (courseData.isNotEmpty()) {
                        classBundles.clear()
                        courseData.forEach { seance ->
                            classBundles.add(
                                ClassBundle(
                                    name = seance.moduleDetails,
                                    room = seance.classRoom,
                                    bgColor = Color.Cyan,
                                    chapter = seance.intitule,
                                    teacher = seance.teacher,
                                    day = dayIndex[seance.dayName] ?: "Unknown",
                                    startingHour = seance.startingTime,
                                    endingHour = seance.endingTime
                                )
                            )
                        }
                        finishedLoading = true
                    }
                    Log.e("schedule", "error fetching data")
                }
            ) {
                val courseData = realm.query<Seance>().find()
                isItUpToDate = true

                Log.d("schedule", "Fetched courses: ${courseData.size}")

                if (courseData.isNotEmpty()) {
                    classBundles.clear()
                    courseData.forEach { seance ->
                        classBundles.add(
                            ClassBundle(
                                name = seance.moduleDetails,
                                room = seance.classRoom,
                                bgColor = Color.Cyan,
                                chapter = seance.intitule,
                                teacher = seance.teacher,
                                day = dayIndex[seance.dayName] ?: "Unknown",
                                startingHour = seance.startingTime,
                                endingHour = seance.endingTime
                            )
                        )
                    }
                    finishedLoading = true
                } else {
                    Log.d("ScheduleScreen", "No courses found")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TitleAppBar(title = "Schedule",)
        },
        floatingActionButton = {
            if (OnlineDataBase.getGroup(ctx) == "Administration"){
                androidx.compose.material3.FloatingActionButton(onClick = { showDialog = true }) {
                    Text("browse")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            HeaderSection(pagerState, if (isItUpToDate) "Up to date" else "Outdated")

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(30.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Time", style = MaterialTheme.typography.titleSmall)
                VerticalDivider(
                    Modifier
                        .height(15.dp)
                        .padding(horizontal = 25.dp)
                )
                Text(text = "Class", style = MaterialTheme.typography.titleSmall)
            }

            if (finishedLoading) {
                ClassList(pagerState, classBundles)
            } else {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 50.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
        if (showDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Change Group") },
                text = {
                    Column {
                        Text("Enter your group code:")
                        androidx.compose.material3.OutlinedTextField(
                            value = newGroup,
                            onValueChange = { newGroup = it.uppercase() },
                            placeholder = { Text("e.g., AM201") }
                        )
                    }
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        grp = newGroup
                        updateTrigger = !updateTrigger
                        showDialog = false
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        showDialog = false
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }

    }
}
