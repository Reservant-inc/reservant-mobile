package reservant_mobile.ui.activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.reservant_mobile.R
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import reservant_mobile.ApplicationService
import reservant_mobile.data.constants.PermissionStrings
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.services.FirebaseService
import reservant_mobile.data.services.UserService
import reservant_mobile.ui.components.NotificationHandler
import reservant_mobile.ui.components.RequestPermission
import reservant_mobile.ui.theme.AppTheme
import reservant_mobile.ui.viewmodels.LoginViewModel

class MainActivity : ComponentActivity() {
    private val fcmService = FirebaseService()
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var isLoading = true
        var startPoint: @Composable ()->Unit
        splashScreen.setKeepOnScreenCondition { isLoading }

        lifecycleScope.launch {
            askNotificationPermission()

            val notificationChannel = NotificationChannel(
                "water_notification",
                "Water",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

             startPoint = if(LoginViewModel().refreshToken()) {
                 if(Roles.RESTAURANT_EMPLOYEE in UserService.UserObject.roles) {
                     { EmployeeHomeActivity() }
                 }
                 else{
                     { HomeActivity() }
                 }
             } else {
                 { LandingActivity() }
             }

            setContent {
                AppTheme {
                    isLoading=false
                    startPoint()

                    RequestPermission(
                        permission = PermissionStrings.NOTIFICATIONS,
                    )

                    val waterNotificationService by remember {
                        mutableStateOf(NotificationHandler(applicationContext))
                    }

                    LaunchedEffect(key1 = Unit) {
                        waterNotificationService.showBasicNotification()
                    }

                }
            }

        }
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (!isGranted) {
            val msg = getString(R.string.message_notification_not_granted)
            Log.d(TAG, msg)
            Toast.makeText(ApplicationService.instance, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ){
                fcmService.saveToken()
            } else{
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        else{
            fcmService.saveToken()
        }
    }
}