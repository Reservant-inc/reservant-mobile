package reservant_mobile.ui.components

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.activity.ComponentActivity.NOTIFICATION_SERVICE
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.getSystemService
import com.example.reservant_mobile.R
import kotlin.random.Random

class NotificationHandler(
    private val context: Context
) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    init {
        setupRestaurantChannel()
        setupFriendsChannel()
        setupEventsChannel()
        setupVisitsChannel()
    }

    //handles these notification types:
    //NotificationRestaurantVerified, NotificationNewRestaurantReview
    private fun setupRestaurantChannel(){
        val notificationChannel = NotificationChannel(
            getString(context, R.string.restaurant_notification_channel_id),
            getString(context, R.string.restaurant_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

    //handles these notification types:
    //NotificationNewFriendRequest, NotificationFriendRequestAccepted
    private fun setupFriendsChannel(){
        val notificationChannel = NotificationChannel(
            getString(context, R.string.friends_notification_channel_id),
            getString(context, R.string.friends_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

    //handles these notification types:
    //NotificationNewParticipationRequest, NotificationParticipationRequestResponse
    private fun setupEventsChannel(){
        val notificationChannel = NotificationChannel(
            getString(context, R.string.events_notification_channel_id),
            getString(context, R.string.events_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

    //handles these notification types:
    //NotificationVisitApprovedDeclined
    private fun setupVisitsChannel(){
        val notificationChannel = NotificationChannel(
            getString(context, R.string.visits_notification_channel_id),
            getString(context, R.string.visits_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }
    
    fun showBasicNotification(){
        val notification= NotificationCompat.Builder(context,"water_notification")
            .setContentTitle("Water Reminder")
            .setContentText("Time to drink a glass of water")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification
        )
    }

}

