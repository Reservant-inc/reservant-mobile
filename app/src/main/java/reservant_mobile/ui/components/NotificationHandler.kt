package reservant_mobile.ui.components

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.reservant_mobile.R
import kotlin.random.Random

class NotificationHandler(
    private val context: Context
) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

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

