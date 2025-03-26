package com.el_aouthmanie.nticapp.ui.screens.anouncmentsScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.el_aouthmanie.nticapp.modules.intities.Announcement
import com.el_aouthmanie.nticapp.ui.compenents.TitleAppBar

@Composable
fun NotificationScreen(navController: NavController? = null, announcements : List<Announcement> = emptyList()) {


    Scaffold(
        containerColor = Color.White,
        topBar = { TitleAppBar(title = "Announcements") }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(announcements) { announcement ->
                NotificationItem(announcement)
            }
        }
    }
}

@Composable
fun NotificationItem(announcement: Announcement) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { expanded = !expanded }
            .padding(12.dp)
            .animateContentSize(TweenSpec(300)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF25D366)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Notification",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = announcement.sender,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = announcement.message,
                color = Color.DarkGray,
                fontSize = 14.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 1
            )
            Text(text = announcement.time, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun DateSeparator(date: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = date,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNotificationScreen() {
    val announcements = listOf(
        Announcement("Admin", "New udpdate available!", "10:30 AM"),
        Announcement("Support", "Server maintenance scheduled.Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata", "Yesterday"),
        Announcement("System", "Security patch applied successfully.", "Monday")
    )
    NotificationScreen(announcements = announcements)
}