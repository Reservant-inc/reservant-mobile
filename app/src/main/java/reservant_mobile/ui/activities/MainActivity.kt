package reservant_mobile.ui.activities

import android.Manifest
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
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.ApplicationService
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.services.FirebaseService
import reservant_mobile.data.services.UserService
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