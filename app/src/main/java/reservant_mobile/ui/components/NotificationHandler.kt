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
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.services.UserService
import kotlin.random.Random

class NotificationHandler (
    private val context: Context
) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    private val restaurantChannelId = getString(context, R.string.restaurant_notification_channel_id)
    private val friendsChannelId = getString(context, R.string.friends_notification_channel_id)
    private val eventsChannelId = getString(context, R.string.events_notification_channel_id)
    private val visitsChannelId = getString(context, R.string.visits_notification_channel_id)

    init {

        if (Roles.CUSTOMER in UserService.UserObject.roles){
            setupFriendsChannel()
            setupEventsChannel()
            setupVisitsChannel()
        }

        val employeeRoles = listOf(
            Roles.RESTAURANT_EMPLOYEE,
            Roles.RESTAURANT_OWNER,
            Roles.RESTAURANT_HALL_EMPLOYEE,
            Roles.RESTAURANT_BACKDOORS_EMPLOYEE
        )

        if (UserService.UserObject.roles.any { employeeRoles.contains(it) }){
            setupRestaurantChannel()
        }


    }

    //handles these notification types:
    //NotificationRestaurantVerified, NotificationNewRestaurantReview
    private fun setupRestaurantChannel(){
        val notificationChannel = NotificationChannel(
            restaurantChannelId,
            getString(context, R.string.restaurant_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

    //handles these notification types:
    //NotificationNewFriendRequest, NotificationFriendRequestAccepted
    private fun setupFriendsChannel(){
        val notificationChannel = NotificationChannel(
            friendsChannelId,
            getString(context, R.string.friends_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

    //handles these notification types:
    //NotificationNewParticipationRequest, NotificationParticipationRequestResponse
    private fun setupEventsChannel(){
        val notificationChannel = NotificationChannel(
            eventsChannelId,
            getString(context, R.string.events_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

    //handles these notification types:
    //NotificationVisitApprovedDeclined
    private fun setupVisitsChannel(){
        val notificationChannel = NotificationChannel(
            visitsChannelId,
            getString(context, R.string.visits_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }
    
    fun showBasicNotification(){
        val notification = NotificationCompat.Builder(context, restaurantChannelId)
            .setContentTitle("Water Reminder")
            .setContentText("Time to drink a glass of water")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            Random.nextInt(), //TODO: save notification ID somewhere ???
            notification
        )
    }

}

