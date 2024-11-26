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
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.models.dtos.NotificationDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.FileService
import reservant_mobile.data.services.NotificationService
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.formatToDateTime
import kotlin.random.Random

class NotificationHandler (
    private val context: Context,
    private val service: NotificationService = NotificationService(),
    private val fileService: FileService = FileService(),
    private val primaryColor: Color
) : Closeable {

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
        }else{
            println("[NOTIFICATIONS] Error occurred while creating session")
        }
    }

    suspend fun awaitNotification(){
        if (session == null){
            println("[NOTIFICATIONS] awaitNotification() was called but session was closed or is null")
            return
        }

        while (true){

            val notification = service.receiveNotificationFromSession(session!!)

            if (!notification.isError && notification.value != null){
                val photo = notification.value.photo?.let {
                    fileService.getImage(it)
                } ?: Result(isError = true, value = null)

                notification.value.notificationType?.let {
                    showBasicNotification(
                        context.getString(it.resId),
                        it.getBody(notification.value.details),
                        photo.value
                    )
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

    override fun close() {
        session?.let {
            it.launch {
                it.close(CloseReason(CloseReason.Codes.GOING_AWAY, ""))
                println("[NOTIFICATION] Websocket session closed")
            }
        }

        session = null
    }

    private fun NotificationDTO.NotificationType.getBody(details: Map<String, JsonElement>?) : String =
        details?.let {
            when {
                this == NotificationDTO.NotificationType.NotificationRestaurantVerified ->
                    "${details["restaurantName"]?.string()} has been verified"
                this == NotificationDTO.NotificationType.NotificationNewRestaurantReview ->
                    "${details["authorName"]?.string()} has reviewed your restaurant - ${details["restaurantName"]?.string()}"
                this == NotificationDTO.NotificationType.NotificationNewFriendRequest ->
                    "${details["senderName"]?.string()} sent you a friend request"
                this == NotificationDTO.NotificationType.NotificationFriendRequestAccepted ->
                    "${details["acceptingUserFullName"]?.string()} accepted your friend request"
                this == NotificationDTO.NotificationType.NotificationNewParticipationRequest ->
                    "${details["senderName"]?.string()} wants to join your event - ${details["eventName"]?.string()}"
                this == NotificationDTO.NotificationType.NotificationParticipationRequestResponse ->
                    if (details["isAccepted"]?.jsonPrimitive?.boolean == true)
                        "${details["creatorName"]?.string()} accepted you to an event - ${details["name"]?.string()}"
                    else
                        "${details["creatorName"]?.string()} rejected you from an event - ${details["name"]?.string()}"
                this == NotificationDTO.NotificationType.NotificationVisitApprovedDeclined ->
                    if (details["isAccepted"]?.jsonPrimitive?.boolean == true)
                        "${details["restaurantName"]?.string()} accepted your visit on " +
                                formatToDateTime(details["date"]?.jsonPrimitive.toString(), "dd-MM-yyyy")
                    else
                        "${details["restaurantName"]?.string()} rejected your visit on " +
                                formatToDateTime(details["date"]?.jsonPrimitive.toString(), "dd-MM-yyyy")
                this == NotificationDTO.NotificationType.NotificationNewMessage ->
                    "${details["authorName"]?.string()}: ${details["contents"]?.string()}"
                this == NotificationDTO.NotificationType.NotificationNewReservation ->
                    "There's a new reservation at ${details["restaurantName"]?.string()} on " +
                            formatToDateTime(details["date"]?.jsonPrimitive.toString(), "dd-MM-yyyy") +
                            " for ${details["numberOfPeople"]}"
                else -> ""
            }
        } ?: ""

    private fun JsonElement.string() : String = this.jsonPrimitive.content
}

