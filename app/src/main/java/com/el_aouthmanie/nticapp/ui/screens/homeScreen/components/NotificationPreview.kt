package com.el_aouthmanie.nticapp.ui.screens.homeScreen.components

import NotificationCard
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import com.el_aouthmanie.nticapp.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.el_aouthmanie.nticapp.modules.intities.Notification
import org.jetbrains.annotations.Async

@Composable
fun NotificationsList(notifications: List<Notification>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "recent :",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(start = 16.dp)
        )

        LazyColumn {
            if (notifications.isEmpty()){

                item {
                    Image(
                        painterResource(R.drawable.no_notification),
                        "something"
                    )
                }
            }

            items(notifications) { notification ->
                AnimatedVisibility(true) {

                    NotificationCard(
                        icon = Icons.Default.Notifications,

                        title = notification.title,
                        subtitle = notification.body
                    )
                }
            }
        }
    }
}

