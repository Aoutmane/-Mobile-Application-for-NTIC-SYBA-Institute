//package com.el_aouthmanie.istanticapp.ui.testing
//
//import android.util.Log
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.slideInVertically
//import androidx.compose.animation.slideOutVertically
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.RowScope
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.PagerState
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.ElevatedCard
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.VerticalDivider
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.SpanStyle
//import androidx.compose.ui.text.buildAnnotatedString
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.withStyle
//import androidx.compose.ui.tooling.preview.Devices
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.el_aouthmanie.istanticapp.modules.OnlineDataBase
//import com.el_aouthmanie.istanticapp.modules.intities.DayPlaning
//import com.el_aouthmanie.istanticapp.modules.realmHandler.RealmManager
//import com.el_aouthmanie.istanticapp.modules.scheduleHandler.DateHelper
//import com.el_aouthmanie.istanticapp.ui.compenents.TitleAppBar
//import io.realm.kotlin.ext.query
//import kotlinx.coroutines.launch
//
//val dayIndex = mapOf(
//    "Lundi" to "MON",
//    "Mardi" to "TUE",
//    "Mercredi" to "WED",
//    "Jeudi" to "THU",
//    "Vendredi" to "FRI",
//    "Samedi" to "SAT",
//)
//
//@Composable
//fun ScheduleScreen() {
//    val onlineDataBase = OnlineDataBase()
//    val scope = rememberCoroutineScope()
//    val pagerState = rememberPagerState { 6 }
//    var updateTrigger by rememberSaveable {
//        mutableStateOf(true)
//    }
//    var isItUpToDate by  rememberSaveable {
//        mutableStateOf(false)
//    }
//    // Use mutableState for courses to trigger recompositions
//    val courses = mutableSetOf<Course>()
//
//
//    var finishedLoading by rememberSaveable { mutableStateOf(false) }
//
//    val realm = RealmManager.realm
//
//    LaunchedEffect(updateTrigger) {
//        // Sync the classes when the screen is first loaded
//        scope.launch {
//
//            onlineDataBase.syncClasses(
//                "AM201",
//                "S2S26",
//                realm,
//                scope,
//                onFailureResponse = {
//
//                    val courseData = realm.query<DayPlaning>().find()
//
//                    if (courseData.isNotEmpty()) {
//                        courses.clear()
//                        courseData.forEach { dayPlaning ->
//                            dayPlaning.seaces.forEach {
//                                courses.add(
//                                    Course(
//                                        name = it.moduleDetails,
//                                        room = it.classRoom,
//                                        bgColor = Color.Cyan,
//                                        chapter = it.intitule,
//                                        teacher = it.teacher,
//                                        day = dayIndex[dayPlaning.dayName] ?: "MON",
//                                        startingHour = it.startingTime,
//                                        endingHour = it.endingTime,
//                                    )
//                                )
//                            }
//                        }
//                        finishedLoading = true
//                    }
//                    Log.e("schedule","error fetching data")
//                }
//            ) {
//                // After sync is done we get the new data from local storage
//                val courseData = realm.query<DayPlaning>().find()
//                isItUpToDate = true
//
//                Log.d("schedule", "Fetched courses: ${courseData.size}")
//
//                if (courseData.isNotEmpty()) {
//                    courses.clear()
//                    courseData.forEach { dayPlaning ->
//                        dayPlaning.seaces.forEach {
//                            courses.add(
//                                Course(
//                                    name = it.moduleDetails,
//                                    room = it.classRoom,
//                                    bgColor = Color.Cyan,
//                                    chapter = it.intitule,
//                                    teacher = it.teacher,
//                                    day = dayIndex[dayPlaning.dayName] ?: "MON",
//                                    startingHour = it.startingTime,
//                                    endingHour = it.endingTime,
//                                )
//                            )
//                        }
//                    }
//                    finishedLoading = true
//                } else {
//                    Log.d("ScheduleScreen", "No courses found")
//                }
//            }
//        }
//    }
//
//
//    Scaffold(
//        topBar = {
//            TitleAppBar(title = "Schedule", modifier = Modifier.clickable {
//                updateTrigger = !updateTrigger
//            })
//        }
//    ) { paddingValues ->
//        Column(modifier = Modifier.padding(paddingValues)) {
//            HeaderSection(pagerState,if(isItUpToDate)"up to date" else "out dated")
//
//            Row(
//                modifier = Modifier
//                    .padding(horizontal = 16.dp)
//                    .height(30.dp)
//                    .fillMaxWidth()
//            ) {
//                Text(text = "Time")
//                VerticalDivider(
//                    Modifier
//                        .height(15.dp)
//                        .padding(horizontal = 25.dp)
//                )
//                Text(text = "Class")
//            }
//
//            if (finishedLoading) {
//                // Display the courses once data is loaded
//                ClassList(pagerState, courses)
//            } else {
//                CircularProgressIndicator(
//                    modifier = Modifier
//                        .padding(top = 50.dp)
//                        .align(Alignment.CenterHorizontally)
//
//                )
//            }
//        }
//    }
//}
//
//
//
//
//@Preview(showBackground = true)
//@Composable
//private fun HeaderPreview() {
//    HeaderSection(pagerState = rememberPagerState {
//        6
//    })
//}
//
//@Composable
//fun ClassList(pagerState: PagerState, courses: MutableSet<Course>) {
//    HorizontalPager(state = pagerState) { pageIndex ->
//        val selectedDay = dayIndex.keys.elementAt(pageIndex) // Get the day name by index
//        val filteredCourses = courses.filter { it.day == dayIndex[selectedDay] } // Filter courses by day
//
//        LazyColumn(modifier = Modifier.fillMaxSize()) {
//            items(filteredCourses) { course ->
//                AnimatedVisibility(
//                    visible = true,
//                    enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(durationMillis = 300)),
//                    exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(durationMillis = 300))
//                ) {
//                    ClassItem(course)
//                }
//                Spacer(modifier = Modifier.height(12.dp))
//            }
//        }
//    }
//}
//
//
//@Composable
//fun ClassItem(course: Course) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp, horizontal = 16.dp)
//    ) {
//        // Time Column
//        Column(
//            Modifier.weight(1f),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                buildAnnotatedString {
//                    withStyle(
//                        SpanStyle(
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 16.sp,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    ) {
//                        appendLine(course.startingHour)
//                    }
//                    withStyle(
//                        SpanStyle(
//                            fontWeight = FontWeight.Normal,
//                            fontSize = 14.sp,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    ) {
//                        append(course.endingHour)
//                    }
//                },
//                textAlign = TextAlign.Center
//            )
//        }
//
//        Spacer(modifier = Modifier.width(12.dp))
//
//        // com.el_aouthmanie.istanticapp.ui.testing.Course Card
//
//        ElevatedCard(
//            shape = RoundedCornerShape(16.dp),
//            elevation = CardDefaults.elevatedCardElevation(6.dp),
//            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
//            modifier = Modifier.weight(6f)
//        ) {
//            Column(
//                modifier = Modifier.padding(16.dp)
//            ) {
//                Text(
//                    course.name,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 18.sp,
//                    color = MaterialTheme.colorScheme.primary
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    course.chapter,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    fontSize = 14.sp
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        course.room,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        fontSize = 14.sp
//                    )
//                }
//                Spacer(modifier = Modifier.height(4.dp))
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        course.teacher,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        fontSize = 14.sp
//                    )
//                }
//            }
//        }
//    }
//}
//
//
//data class Course(
//
//    val name: String,
//    val chapter: String,
//    val room: String,
//    val teacher: String,
//    val bgColor: Color,
//
//    val day: String,
//    val startingHour: String,
//    val endingHour: String
//)
//
//@Preview(showBackground = true, device = Devices.PIXEL_7)
//@Composable
//fun PreviewScheduleScreen() {
//    ScheduleScreen()
//}
//
//@Composable
//fun RowScope.DayText(day: String, pagerState: PagerState? = null, index : Int) {
//    val isSaturday = day.lowercase() == "sat"
//
//    Surface(
//        color = if (pagerState?.currentPage == index) Color(0xFF4CAF50) else MaterialTheme.colorScheme.surface,
//        shape = RoundedCornerShape(12.dp),
//        tonalElevation = 4.dp, // Adds a subtle shadow effect
//        modifier = Modifier
//            .padding(4.dp)
//            .weight(1f)
//            .height(50.dp)
//    ) {
//        val scope = rememberCoroutineScope()
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//
//                .clickable {
//                    pagerState?.let {
//                        scope.launch {
//                            it.animateScrollToPage(index)
//                        }
//                    }
//                },
//            contentAlignment = Alignment.Center
//        ) {
//
//            androidx.compose.animation.AnimatedVisibility(true){
//                fadeIn(initialAlpha = 0f)
//                Text(
//                    text = day.uppercase(),
//                    color = if (isSaturday) Color.White else MaterialTheme.colorScheme.onSurface,
//                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
//                    textAlign = TextAlign.Center,
//                )
//            }
//        }
//    }
//}
//
//
//
//
////--------------------------------------
//
//
//
//
//
//
//
//
//
//
//@Composable
//fun HeaderSection(pagerState: PagerState,message : String = "") {
//
//    Column(
//        modifier = Modifier
//            .padding(16.dp)
//            .fillMaxWidth()
//    ) {
//        // Date Text
//        Text(
//            text = DateHelper.getCurrentDate().toString(),
//            fontSize = 30.sp,
//            fontWeight = FontWeight.Bold,
//            style = MaterialTheme.typography.bodyLarge // Apply material typography
//        )
//
//        // Day Text
//        Text(
//            text = DateHelper.getCurrentDayFormatted(),
//            fontSize = 14.sp,
//            color = Color.Gray,
//            style = MaterialTheme.typography.bodyMedium // Apply material typography
//        )
//
//        // "Outdated" Text (aligned right and styled)
//        Text(
//            text = message,
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.End),
//            color = MaterialTheme.colorScheme.error, // Use material design's error color
//            fontWeight = FontWeight.Bold,
//            fontSize = 14.sp,
//            textAlign = TextAlign.End
//        )
//
//        // Horizontal Divider
//        HorizontalDivider(Modifier.padding(vertical = 10.dp))
//
//        // Spacer for spacing between elements
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Weekdays Row
//        val weekDays = DateHelper.getWeekDays()
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween // Spread the days across the row
//        ) {
//            weekDays.forEachIndexed { index, day ->
//                DayText(day = day, pagerState = pagerState, index = index)
//            }
//        }
//
//        // Another Divider
//        HorizontalDivider(Modifier.padding(vertical = 10.dp))
//    }
//}
//
//@Composable
//fun HorizontalDivider(modifier: Modifier = Modifier) {
//    HorizontalDivider(
//        modifier = modifier,
//        thickness = 1.dp,
//        color = Color.LightGray
//    )
//}
//
//@Composable
//fun DayText(day: String, pagerState: PagerState, index: Int) {
//    Text(
//        text = day,
//        modifier = Modifier
//            .padding(4.dp)
//            .clickable {
//                // Handle day click logic, perhaps update pagerState to show a specific page
//            },
//        fontSize = 16.sp,
//        fontWeight = FontWeight.Normal,
//        color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else Color.Black
//    )
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
