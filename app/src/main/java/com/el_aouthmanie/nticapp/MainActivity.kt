package com.el_aouthmanie.nticapp


import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

import com.el_aouthmanie.nticapp.modules.realmHandler.RealmManager
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "Token: $token")
            } else {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
            }
        }
//        createNotificationChannel()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
//        }
        RealmManager.initialize(this)

        @Suppress("all")
//        val periodicWorkRequest = PeriodicWorkRequest.Builder(
//            MyWorker::class.java,
//            10, TimeUnit.SECONDS  // This specifies the repeat interval (1 hour)
//        ).build()

//        // Get the WorkManager instance and enqueue the periodic work
//        WorkManager.getInstance(applicationContext).enqueue(periodicWorkRequest)

        setContent {
            val context = LocalContext.current
//            val navContrller = rememberNavController()
            MainScreen(
                )
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "student_channel",
                "Student Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for student notifications"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

}
@Preview(device = Devices.TABLET)
@Composable
fun helloWorld(modifier: Modifier = Modifier) {
    val context = LocalContext.current

//    val db = DatabaseHelper(context)


//            val navContrller = rememberNavController()
    MainScreen(
    )
}