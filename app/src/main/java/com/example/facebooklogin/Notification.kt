package com.example.facebooklogin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.facebooklogin.ui.MainActivity
import kotlin.random.Random

class Notification {
    private val ADMIN_CHANNEL_ID = "admin_channel"

    fun pushNotification(context: Context) {

        val intent = Intent(context, MainActivity::class.java)


        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = 2558


        /*
        Apps targeting SDK 26 or above (Android O) must implement notification channels and add its notifications
        to at least one of them.
      */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, ADMIN_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setAutoCancel(false)
            .setSound(notificationSoundUri)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setStyle(
                NotificationCompat.MessagingStyle("Me")
                    .setConversationTitle("Team lunch")
                    .addMessage("Hi", System.currentTimeMillis().toLong(), "Me") // Pass in null for user.
                    .addMessage("What's up?", System.currentTimeMillis().toLong(), "Coworker")
                    .addMessage("Not much", System.currentTimeMillis().toLong(), "Me")
                    .addMessage("How about lunch?", System.currentTimeMillis().toLong(), "Coworker")
                    .addMessage("Done!", System.currentTimeMillis().toLong(), "Me")

            )

        //Set notification color to match your app color template
        notificationBuilder.color = context.resources.getColor(R.color.purple_200)
        notificationManager.notify(notificationID, notificationBuilder.build())
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannelName = "New notification"
        val adminChannelDescription = "Device to device notification"

        val adminChannel: NotificationChannel = NotificationChannel(
            ADMIN_CHANNEL_ID,
            adminChannelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(adminChannel)
    }
}