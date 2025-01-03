package reservant_mobile.ui.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import reservant_mobile.ui.components.DeliveryContent
import reservant_mobile.ui.components.DineInContent
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.TakeawayContent
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.viewmodels.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantReservationActivity(restaurantId: Int, navController: NavHostController) {
    val reservationViewModel = viewModel<ReservationViewModel>()


    val navControllerSummary = rememberNavController()

    NavHost(
        navController = navControllerSummary,
        startDestination = RestaurantRoutes.Reservation(restaurantId = restaurantId)
    ) {
        composable<RestaurantRoutes.Reservation> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { },
                        actions = {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                IconButton(
                                    onClick = { navController.popBackStack() },
                                    modifier = Modifier.align(Alignment.CenterStart)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.label_back)
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.label_reservation),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .verticalScroll(rememberScrollState())
                        ) {
                            FloatingTabSwitch(
                                pages = listOf(
                                    stringResource(R.string.label_dine_in) to {
                                        DineInContent(
                                            navController = navControllerSummary,
                                            viewModel = reservationViewModel,
                                            modifier = Modifier.padding(top = 88.dp)
                                        )
                                    },
                                    stringResource(R.string.label_delivery) to {
                                        DeliveryContent(
                                            navController = navControllerSummary,
                                            viewModel = reservationViewModel,
                                            modifier = Modifier.padding(top = 88.dp)
                                        )
                                    },
                                    stringResource(R.string.label_takeaway) to {
                                        TakeawayContent(
                                            navController = navControllerSummary,
                                            viewModel = reservationViewModel,
                                            modifier = Modifier.padding(top = 88.dp)
                                        )
                                    }
                                ),
                                paneScroll = false
                            )
                        }
                    }
                }
            }
        }
        composable<RestaurantRoutes.Summary> {
            OrderSummaryActivity(
                reservationViewModel = reservationViewModel,
                navController = navController
            )
        }
    }
}
