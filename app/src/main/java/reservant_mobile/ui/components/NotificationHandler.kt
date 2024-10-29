package reservant_mobile.ui.components

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import androidx.activity.ComponentActivity.NOTIFICATION_SERVICE
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.getSystemService
import com.example.reservant_mobile.R
import kotlinx.coroutines.coroutineScope
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.FileService
import reservant_mobile.data.services.NotificationService
import reservant_mobile.data.services.UserService
import kotlin.coroutines.coroutineContext
import kotlin.random.Random

class NotificationHandler(
    private val context: Context,
    private val service: NotificationService = NotificationService(),
    private val fileService: FileService = FileService()
) {

    private val restaurantChannelId = getString(context, R.string.restaurant_notification_channel_id)
    private val friendsChannelId = getString(context, R.string.friends_notification_channel_id)
    private val eventsChannelId = getString(context, R.string.events_notification_channel_id)
    private val visitsChannelId = getString(context, R.string.visits_notification_channel_id)

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    private var channelsReady = false

    init {
        setupChannels()
    }

    private fun setupChannels(){
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

        channelsReady = true
    }


    private fun setupChannel(
        channelId: String,
        channelName: String,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ){
        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    //handles these notification types:
    //NotificationRestaurantVerified, NotificationNewRestaurantReview
    private fun setupRestaurantChannel(){
        setupChannel(restaurantChannelId, getString(context, R.string.restaurant_notification_channel_name))
    }

    //handles these notification types:
    //NotificationNewFriendRequest, NotificationFriendRequestAccepted
    private fun setupFriendsChannel(){
        setupChannel(friendsChannelId, getString(context, R.string.friends_notification_channel_name))
    }

    //handles these notification types:
    //NotificationNewParticipationRequest, NotificationParticipationRequestResponse
    private fun setupEventsChannel(){
        setupChannel(eventsChannelId, getString(context, R.string.events_notification_channel_name))
    }

    //handles these notification types:
    //NotificationVisitApprovedDeclined
    private fun setupVisitsChannel(){
        setupChannel(visitsChannelId, getString(context, R.string.visits_notification_channel_name))
    }

    suspend fun awaitNotification(){
        val session = service.getNotificationSession()

        if (!session.isError && session.value != null){
            val session = session.value

            while (true){
                val notification = service.receiveNotificationFromSession(session)

                if (!notification.isError && notification.value != null){

                    val notification = notification.value

                    val photo = notification.photo?.let {
                        fileService.getImage(it)
                    } ?: Result(isError = true, value = null)


                    showBasicNotification(
                        notification.notificationType.toString(),
                        "Parse content here", // TODO add content parsing
                        photo.value
                    )

                }

            }

        }

    }

    private fun showBasicNotification(title: String, content: String, photo: Bitmap?){
        val notification = NotificationCompat.Builder(context, restaurantChannelId)
            .setContentTitle(title)
            .setContentText(content)
            .setLargeIcon(photo)
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

