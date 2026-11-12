package com.bqsqwad.howdoilook.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bqsqwad.howdoilook.R

object HowDoILookNotifications {
    const val CHANNEL_ID = "how_do_i_look_reminders"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Style reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Optional reminders for wardrobe plans and clothing care"
            }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    /**
     * Shows an opt-in reminder. The small icon deliberately uses a white,
     * monochrome status glyph, as required by Android notification rendering.
     * The full-colour launcher logo remains the app identity in the launcher.
     */
    fun showReminder(context: Context, title: String, message: String, notificationId: Int = 4101): Boolean {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return false

        createChannels(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_how_do_i_look)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        return try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
            true
        } catch (_: SecurityException) {
            false
        }
    }
}
