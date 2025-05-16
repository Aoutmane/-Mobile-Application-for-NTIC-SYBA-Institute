package com.el_aouthmanie.nticapp.ui.screens.homeScreen


import android.app.Activity
import android.content.Context
import android.icu.util.LocaleData
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.el_aouthmanie.nticapp.R
import com.el_aouthmanie.nticapp.globals.CONSTANTS
import com.el_aouthmanie.nticapp.modules.OnlineDataBase
import com.el_aouthmanie.nticapp.modules.intities.Seance
import com.el_aouthmanie.nticapp.modules.realmHandler.RealmManager
import com.el_aouthmanie.nticapp.ui.screens.homeScreen.components.HeaderSection
import com.el_aouthmanie.nticapp.ui.screens.homeScreen.components.NotificationsList
import com.el_aouthmanie.nticapp.ui.screens.homeScreen.components.RectangleButtonWithIcon
import com.el_aouthmanie.nticapp.ui.screens.homeScreen.components.ScheduleCard
import com.el_aouthmanie.nticapp.ui.theme.backgroundWhite
import com.el_aouthmanie.nticapp.ui.theme.primaryBlue
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.random.Random
import androidx.core.content.edit
import com.el_aouthmanie.nticapp.modules.intities.Notification
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate
import java.time.format.DateTimeParseException

val mod = "S2S33"
val URL = "profile_uri"
val PREF_NAME = "profile_prefs"

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController) {
    val ctx = LocalContext.current
    val sharedPrefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    val quote = getRandomEducationQuote()
    val savedUri = remember {
        mutableStateOf(sharedPrefs.getString(URL, null))
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                savedUri.value = it.toString()
                sharedPrefs.edit() { putString(URL, it.toString()) }
            }
        }
    }

    var nextDayClass by remember { mutableStateOf(Seance()) }
    var i = remember { mutableIntStateOf(0) }

    var showProfileDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }


    val db = OnlineDataBase
    val scope = rememberCoroutineScope()

    scheduleUIUpdate(i)
    LaunchedEffect(i) {
        db.syncClasses(
            OnlineDataBase.getGroup(ctx), mod, RealmManager.realm, scope,
            onFailureResponse = {
                val fetchedClass = db.getNextClass(RealmManager.realm, scope)
                nextDayClass = fetchedClass ?: Seance()
            }) {
            val fetchedClass = db.getNextClass(RealmManager.realm, scope)
            nextDayClass = fetchedClass ?: Seance()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        Modifier.background(backgroundWhite),
        topBar = {
            HeaderSection(
                stringResource(R.string.hello),
                OnlineDataBase.getName(ctx, false),
                quote,
                savedUri.value
            ) {
                showProfileDialog = true
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { message ->
                Snackbar(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = message.visuals.message,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        floatingActionButton = {
            if (OnlineDataBase.getGroup(ctx) == "Administration") {
                FloatingActionButton(onClick = {
                    showNotificationDialog = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "send notification"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box {
            Canvas(modifier = Modifier.fillMaxSize()) {
                repeat(3) {
                    drawLine(
                        color = primaryBlue.copy(alpha = 0.1f),
                        start = center.copy(
                            x = Random.nextFloat() * size.width,
                            y = Random.nextFloat() * size.height
                        ),
                        end = center.copy(
                            x = Random.nextFloat() * size.width,
                            y = Random.nextFloat() * size.height
                        ),
                        strokeWidth = Random.nextFloat() * 6f + 2f
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {

                HorizontalPager(
                    state = rememberPagerState { 1 },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp)),
                ) { i ->
                    ScheduleCard(nextDayClass)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    RectangleButtonWithIcon(icon = Icons.Outlined.Star, containerColor = Color.Gray) {

                    }
                    RectangleButtonWithIcon(icon = Icons.Outlined.DateRange) {
                        navController.navigate(CONSTANTS.Screens.SCHEDULE)
                    }
                    RectangleButtonWithIcon(icon = Icons.Outlined.Notifications) {
                        navController.navigate(CONSTANTS.Screens.ANNOUNCMENTS)
                    }
                }
                var isLoading by remember { mutableStateOf(true) }
                val notificationsFlow = remember {
                    RealmManager.realm.query<Notification>(Notification::class).asFlow()
                        .map { it.list }
                }

                val notifications by notificationsFlow
                    .onEach { isLoading = false }
                    .collectAsState(initial = emptyList<Notification>())

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val today = LocalDate.now()

                    val recentNotifications = notifications.filter {
                        try {
                            val notificationDate = LocalDate.parse(it.createdAt.take(10))
                            val daysBetween =
                                java.time.temporal.ChronoUnit.DAYS.between(notificationDate, today)
                            daysBetween in 0..2
                        } catch (e: DateTimeParseException) {
                            false
                        }
                    }.reversed()

                    if (recentNotifications.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.no_notification),
                                    contentDescription = "No Notifications",
                                    modifier = Modifier.size(100.dp)
                                )
                                Text (
                                    text = "No recent notifications",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    } else {
                        NotificationsList(notifications = recentNotifications)
                    }
                }


            }
        }
    }

    NotificationDialog(
        showNotificationDialog,
        onDismiss = { showNotificationDialog = false },
        onSend = { notification ->
            var grp = ""
            val newNotification = Notification().apply {
                this.title = notification.title
                this.body = notification.body
                this.sender = notification.sender

            }
            OnlineDataBase.sendNotificationToServer(
                group = notification.type,
                title = newNotification.title,
                body = newNotification.body,
                onSuccess = {
                    scope.launch {
                    snackbarHostState.showSnackbar("Notification sent")
                }
                },
                onError = { scope.launch {
                    snackbarHostState.showSnackbar("Notification not sent!")
                }})
            RealmManager.realm.writeBlocking {
                copyToRealm(newNotification)
            }

        }
    )

    ProfileDialog(
        showProfileDialog,
        onDismiss = { showProfileDialog = false },
        savedUri = savedUri.value,
        launcher = launcher,
        navController
    )

}

@Composable
fun ProfileDialog(
    showProfileDialog: Boolean,
    onDismiss: () -> Unit,
    savedUri: String?,
    launcher: androidx.activity.result.ActivityResultLauncher<android.content.Intent>,
    navController: NavController
) {
    if (!showProfileDialog) return

    val ctx = LocalContext.current
    val uriHandler = LocalUriHandler.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "More info",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://www.ofppt.ma")
                    }
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Profile header: name and image clickable to pick new image
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = OnlineDataBase.getName(ctx, true),
                        style = MaterialTheme.typography.titleMedium
                    )

                    val painter = if (savedUri != null) {
                        rememberAsyncImagePainter(savedUri)
                    } else {
                        painterResource(id = R.drawable.profile)
                    }

                    Image(
                        painter = painter,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .clickable {
                                ImagePicker.with(ctx as ComponentActivity)
                                    .cropSquare()
                                    .compress(1024)
                                    .maxResultSize(512, 512)
                                    .createIntent { intent -> launcher.launch(intent) }
                            }
                    )
                }

                // Menu options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // when ya have time dev them ;)
                    listOf("Profile", "Clubs").forEach { label ->
                        Text(
                            text = label,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "About",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable {
                                onDismiss()
                                navController.navigate("about")
                            }
                    )
                    Text(
                        text = "Settings",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable {
                                onDismiss()
                                navController.navigate(
                                    "settings"
                                )
                            }
                    )
                }


            }
        }
    )
}


fun scheduleUIUpdate(i: MutableState<Int>) {
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

    }

}

@Composable
fun NotificationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSend: (Notification) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var sender = OnlineDataBase.getName(LocalContext.current, true)
    var grp by remember { mutableStateOf("") }
    var logoName by remember { mutableStateOf("Notification") }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    val notification = Notification().apply {
                        this.title = title
                        this.body = body
                        this.sender = sender
                        this.logoName = logoName
                        this.type = grp
                    }
                    onSend(notification)
                    onDismiss()
                }) {
                    Text("Send")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            },
            title = { Text("Send Notification") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = body,
                        onValueChange = { body = it },
                        label = { Text("Body") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = sender,
                        onValueChange = { },

                        label = { Text("Sender") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = grp,
                        onValueChange = { grp = it },
                        label = { Text("Group :") },
                        modifier = Modifier.fillMaxWidth()
                    )
//                    Box(modifier = Modifier.fillMaxWidth()) {
//                        OutlinedTextField(
//                            value = logoName,
//                            onValueChange = {},
//                            label = { Text("Icon") },
//                            readOnly = true,
//                            trailingIcon = {
//                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
//                            },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable { expandedLogo = true }
//                        )
//                        DropdownMenu(
//                            expanded = expandedLogo,
//                            onDismissRequest = { expandedLogo = false }
//                        ) {
//                            logoOptions.forEach { option ->
//                                DropdownMenuItem(
//                                    text ={
//                                        Text(option)
//                                    },
//                                    onClick = {
//                                        logoName = option
//                                        expandedLogo = false
//                                    }
//                                )
//                            }
//                        }
//                    }
                }
            }
        )
    }
}


fun getRandomEducationQuote(): String {
    val quotes = listOf(
        "Education is the most powerful weapon which you can use to change the world. – Nelson Mandela",
        "The roots of education are bitter, but the fruit is sweet. – Aristotle",
        "An investment in knowledge pays the best interest. – Benjamin Franklin",
        "Education is not preparation for life; education is life itself. – John Dewey",
        "The purpose of education is to replace an empty mind with an open one. – Malcolm Forbes",
        "Live as if you were to die tomorrow. Learn as if you were to live forever. – Mahatma Gandhi",
        "The function of education is to teach one to think intensively and to think critically. – Martin Luther King Jr.",
        "Education is the key to unlock the golden door of freedom. – George Washington Carver",
        "Develop a passion for learning. If you do, you will never cease to grow. – Anthony J. D’Angelo",
        "The beautiful thing about learning is that no one can take it away from you. – B.B. King"
    )
    return quotes.random()
}
