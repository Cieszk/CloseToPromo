package pl.cieszk.closetopromo.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import pl.cieszk.closetopromo.R

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                Log.d("Geofencing", "Geofencing error code: " + geofencingEvent.errorCode)
                return
            }

            val geofenceTransition = geofencingEvent.geofenceTransition
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                sendNotification(context, "You are near a store with a promotion, check your discounts!")
            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                sendNotification(context, "You have left the area of the store where there is a promotion, consider returning to take advantage of the deals.!")
            }
        }
    }

    private fun sendNotification(context: Context, message: String) {
        val notificationManager = ContextCompat.getSystemService(
            context, NotificationManager::class.java) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Geofence Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for Geofence notifications"
        }
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Geofence Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {
        private const val CHANNEL_ID = "geofence_channel"
        private const val NOTIFICATION_ID = 1
    }
}