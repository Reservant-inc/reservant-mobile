package reservant_mobile.ui.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import reservant_mobile.ui.components.DeliveryContent
import reservant_mobile.ui.components.DineInContent
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.TakeawayContent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantReservationActivity(navController: NavHostController) {
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
                            text = "Rezerwacja stolika",
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
                    .padding(end = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .height(680.dp)
                        .width(360.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .verticalScroll(rememberScrollState())
                ) {
                    FloatingTabSwitch(
                        pages = listOf(
                            "Na miejscu" to {
                                DineInContent(
                                    onDineInClick = { /* Handle dine-in click */ },
                                    modifier = Modifier.padding(top = 88.dp)
                                )
                            },
                            "Dostawa" to {
                                DeliveryContent(
                                    onDeliveryClick = { /* Handle delivery click */ },
                                    modifier = Modifier.padding(top = 88.dp)
                                )
                            },
                            "Odbiór" to {
                                TakeawayContent(
                                    onTakeawayClick = { /* Handle takeaway click */ },
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

@Preview(showBackground = true)
@Composable
fun Preview() {
    RestaurantReservationActivity(navController = rememberNavController())
}