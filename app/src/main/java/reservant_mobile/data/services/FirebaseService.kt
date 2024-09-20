package reservant_mobile.data.services

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import reservant_mobile.data.constants.PrefsKeys

class FirebaseService: FirebaseMessagingService() {
    private val localDataService = LocalDataService()

    suspend fun acquireToken(): String?{
        return try{
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.w(TAG, "Fetching FCM registration token failed", e)
            null
        }
    }

    suspend fun saveToken(){
        val token = acquireToken()
        token?.let { localDataService.saveData(PrefsKeys.FCM_TOKEN, it) }
    }


    @OptIn(DelicateCoroutinesApi::class)
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        GlobalScope.launch {
            localDataService.saveData(PrefsKeys.FCM_TOKEN, token)
        }
     }

}