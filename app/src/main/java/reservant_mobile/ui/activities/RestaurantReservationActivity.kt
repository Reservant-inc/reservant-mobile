package reservant_mobile.ui.activities

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.reservant_mobile.R
import reservant_mobile.ui.components.OrderFormContent
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.viewmodels.ReservationViewModel
import reservant_mobile.ui.viewmodels.RestaurantDetailViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantReservationActivity(
    restaurantId: Int,
    navController: NavHostController,
    reservationViewModel: ReservationViewModel,
    restaurantDetailVM: RestaurantDetailViewModel,
    isReservation: Boolean
) {
    val navControllerSummary = rememberNavController()

    NavHost(
        navController = navControllerSummary,
        startDestination = RestaurantRoutes.Reservation(restaurantId = restaurantId, isReservation = isReservation)
    ) {
        composable<RestaurantRoutes.Reservation> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(id = R.string.label_reservation)) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.label_back)
                                )
                            }
                        }
                    )
                }
            ) {
                OrderFormContent(
                    navController = navControllerSummary,
                    reservationViewModel = reservationViewModel,
                    restaurant = restaurantDetailVM.restaurant!!,
                    getMenuPhoto = { photoString ->
                        restaurantDetailVM.getPhoto(photoString)
                    },
                    isReservation = isReservation
                )
            }
        }
        composable<RestaurantRoutes.Summary> {
            OrderSummaryActivity(
                restaurantId = it.toRoute<RestaurantRoutes.Summary>().restaurantId,
                reservationViewModel = reservationViewModel,
                navController = navController,
                isReservation = it.toRoute<RestaurantRoutes.Summary>().isReservation,
            )
        }
    }
}
