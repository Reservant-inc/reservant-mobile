package reservant_mobile.ui.components

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getString
import com.example.reservant_mobile.R
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.utils.io.core.Closeable
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.models.dtos.NotificationDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.FileService
import reservant_mobile.data.services.NotificationService
import reservant_mobile.data.services.UserService
import kotlin.random.Random

class NotificationHandler (
    private val context: Context,
    private val service: NotificationService = NotificationService(),
    private val fileService: FileService = FileService(),
    private val primaryColor: Color
) {

    private val restaurantChannelId = getString(context, R.string.restaurant_notification_channel_id)
    private val friendsChannelId = getString(context, R.string.friends_notification_channel_id)
    private val eventsChannelId = getString(context, R.string.events_notification_channel_id)
    private val visitsChannelId = getString(context, R.string.visits_notification_channel_id)

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    private var channelsReady = false

    private var session: DefaultClientWebSocketSession? = null

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
        setupChannel(restaurantChannelId, getString(context, R.string.restaurant_notification_channel_name), NotificationManager.IMPORTANCE_HIGH)
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

    suspend fun createSession(){
        val res = service.getNotificationSession()
        if (!res.isError && res.value != null){
            session = res.value
            println("[NOTIFICATIONS] Websocket session created")
        } else {
            println("[NOTIFICATIONS] Error occurred while creating session")
        }
    }

    suspend fun awaitNotification(){
        if (session == null){
            println("[NOTIFICATIONS] awaitNotification() was called but session was closed or is null")
            return
        }

        session?.let {
            while (it.isActive){
                val notification = service.receiveNotificationFromSession(it)
                if (!notification.isError && notification.value != null){
                    println("[NOTIFICATIONS] notification received, displaying")
                    val photo = notification.value.photo?.let {
                        fileService.getImage(it)
                    } ?: Result(isError = true, value = null)

                    notification.value.notificationType?.let {

                        val pair = it.getContent(notification.value.details)
                        showBasicNotification(
                            pair.first,
                            pair.second,
                            photo.value
                        )

                    }

                }

            }

        }


    }

    private fun showBasicNotification(title: String, content: String, photo: Bitmap? = null){
        val notification = NotificationCompat.Builder(context, restaurantChannelId)
            .setContentTitle(title)
            .setContentText(content)
            .setColor(primaryColor.toArgb())
            .setSmallIcon(R.drawable.ic_logo)
            .setLargeIcon(photo)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification
        )
    }

    suspend fun close() {
        session?.let {
            it.close(CloseReason(CloseReason.Codes.GOING_AWAY, ""))
            println("[NOTIFICATIONS] Websocket session closed")
        }

        session = null

    }


    private fun NotificationDTO.NotificationType.getContent(details: Map<String, JsonElement>?) : Pair<String, String> =
        details?.let {
            return context.getString(
                this.getTitleResource(details), *this.getTitleArguments(details)
            ) to context.getString(
                this.getBodyResource(details), *this.getBodyArguments(details)
            )
        } ?: ("" to "")

}

