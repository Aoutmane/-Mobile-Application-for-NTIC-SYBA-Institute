package com.el_aouthmanie.nticapp.ui.screens.homeScreen


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.el_aouthmanie.nticapp.R
import com.el_aouthmanie.nticapp.modules.OnlineDataBase
import com.el_aouthmanie.nticapp.modules.intities.Seance
import com.el_aouthmanie.nticapp.modules.realmHandler.RealmManager
import com.el_aouthmanie.nticapp.ui.compenents.ImagePager
import com.el_aouthmanie.nticapp.ui.compenents.NotificationItem
import com.el_aouthmanie.nticapp.ui.compenents.notificationList
import com.el_aouthmanie.nticapp.ui.screens.homeScreen.components.SchedulePreview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar


@Composable
fun HomeScreen() {
    val db = OnlineDataBase()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var nextDayClass by remember { mutableStateOf(Seance()) }
    var i = remember { mutableStateOf(0) }
    scheduleUIUpdate(i)
    LaunchedEffect(i) {
        db.syncClasses("AM201", "S2S26", RealmManager.realm, scope, onFailureResponse = {
            val fetchedClass = db.getNextClass(RealmManager.realm, scope)
            nextDayClass = fetchedClass ?: Seance()
        }) {
            val fetchedClass = db.getNextClass(RealmManager.realm, scope)

            nextDayClass = fetchedClass ?: Seance()
        }
    }






    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            ConstraintLayout {
                val (topBar, group, eventCard) = createRefs()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(bottomEnd = 10.dp, bottomStart = 10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF7676EC),
                                    Color(0xFF2424E6)
                                )
                            )
                        )
                        .constrainAs(topBar) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                        }
                ) {
                    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

                        // **Profile Picture & Name**
                        Row(
                            modifier = Modifier
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(
                                text = "Abderrahman ABOUELKACIM",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // **User ID**

                    }
                }
                Text(
                    text = "OAM201",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.constrainAs(group) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(eventCard.top, 10.dp)
                    }
                )

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .constrainAs(eventCard) {
                            top.linkTo(topBar.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(topBar.bottom)
                        }
                        .clip(RoundedCornerShape(10.dp)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    SchedulePreview(
                        nextClass = nextDayClass,
                        scope = scope
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState,

                snackbar = { message ->
                    Snackbar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        content = {
                            Text(text = message.visuals.message, textAlign = TextAlign.Center)
                        }
                    )
                }

            )
        },

//        floatingActionButton = {
//            FloatingActionButton(onClick = { exitProcess(0) }) {
//                Text(text = "aha")
//            }
//        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {


            // Image Slider
            ImagePager(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp)),
                images = List(5){"https://picsum.photos/300/200"}
            )

            // Recent Section
            Text(
                text = "Recent",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                color = MaterialTheme.colorScheme.primary
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(notificationList.size) { index ->
                    val notification = notificationList[index]

                    NotificationItem(
                        profileImage = notification.imageRes,
                        title = notification.title,
                        sender = notification.sender,
                        message = notification.message
                    ) {
                        // Handle click
                    }
                }
            }
        }
    }
}

//
@Preview(showBackground = true, showSystemUi = true, device = Devices.DEFAULT)
@Composable
fun Hehe(modifier: Modifier = Modifier) {
    HomeScreen()
}
fun scheduleUIUpdate(i : MutableState<Int>) {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 18)
        set(Calendar.MINUTE, 22)
        set(Calendar.SECOND, 0)
    }

    if (now.after(target)) {
        target.add(Calendar.DAY_OF_MONTH, 1) // Move to next day if time has passed
    }

    val delayMillis = target.timeInMillis - now.timeInMillis

    CoroutineScope(Dispatchers.Main).launch {
        delay(delayMillis)
        i.value++
        Log.d("j","feffefe")
    }

}