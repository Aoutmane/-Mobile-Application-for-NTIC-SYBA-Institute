package com.el_aouthmanie.nticapp.modules.firebaseHandling

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.NotificationCompat
import com.el_aouthmanie.nticapp.R
import com.el_aouthmanie.nticapp.modules.OnlineDataBase
import com.el_aouthmanie.nticapp.modules.intities.Notification
import com.el_aouthmanie.nticapp.modules.realmHandler.RealmManager
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class MyFirebaseMessagingService : FirebaseMessagingService() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        Log.d("FCM", "Message received: ${remoteMessage.data}")
        val date = remoteMessage.data.getOrDefault("timestamp", LocalDate.now().toString())
        remoteMessage.notification?.let {
            Log.d("FCM", "Notification Title: ${it.title}, Body: ${it.body}")

            if(OnlineDataBase.notificationsEnabled(applicationContext)){
                showNotification(it.title, it.body)
            }


            CoroutineScope(Dispatchers.IO).launch {
                OnlineDataBase.addAnnouncmentToHistory(
                    RealmManager.realm,
                    Notification().apply {
                        title = it.title ?: "notification"
                        body = it.body ?: "no details"
                        createdAt = date
                    }

                )

            }
        }

        }



    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val ctx = applicationContext
        val group = OnlineDataBase.getGroup(ctx)
        FirebaseMessaging.getInstance().subscribeToTopic(group)
    }

    private fun showNotification(title: String?, message: String?) {



        val builder = NotificationCompat.Builder(this, "student_channel")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(30, builder.build())
    }

    fun subscribeToGroup(grp : String){

    }

}
