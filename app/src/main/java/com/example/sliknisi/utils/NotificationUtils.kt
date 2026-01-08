package com.example.sliknisi.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.lib.Landmark
import com.example.sliknisi.MainActivity
import com.example.sliknisi.R

object NotificationUtils {

    private const val CHANNEL_ID = "com.example.sliknisi.LANDMARK_PROXIMITY"
    private const val CHANNEL_NAME = "Landmark Proximity"
    private var notificationId = 1000

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Notifications when approaching landmarks"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showProximityNotification(context: Context, landmark: Landmark, distance: Float) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("landmark_id", landmark.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            landmark.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val distanceText = DistanceUtils.formatDistanceWithSuffix(distance)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_location)
            .setContentTitle("📍 ${landmark.name}")
            .setContentText("You're $distanceText! ⭐ ${landmark.pointValue} points")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId++, notification)
    }
}