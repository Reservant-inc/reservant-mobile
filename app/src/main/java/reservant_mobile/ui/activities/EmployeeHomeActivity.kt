@file:Suppress("IMPLICIT_CAST_TO_ANY")

package reservant_mobile.ui.activities

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.TableBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.BottomNavItem
import reservant_mobile.ui.components.BottomNavigation
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.ui.navigation.AuthRoutes
import reservant_mobile.ui.navigation.EmployeeRoutes
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.theme.AppTheme
import reservant_mobile.ui.viewmodels.EmployeeHomeViewModel

@Composable
fun EmployeeHomeActivity() {
    val empHomeVM = viewModel<EmployeeHomeViewModel>()
    val innerNavController = rememberNavController()
    val bottomBarState = remember { (mutableStateOf(false)) }


    val isSystemInDarkMode = isSystemInDarkTheme()

    var darkTheme by remember {
        mutableStateOf(isSystemInDarkMode)
    }

    val items = listOfNotNull(
        BottomNavItem.Employee,
        BottomNavItem.Profile
    )

    AppTheme (darkTheme = darkTheme) {
        Scaffold(
            bottomBar = {
                BottomNavigation(
                    navController =  innerNavController,
                    bottomBarState = bottomBarState,
                    items = items
                )
            }
        ){
            LaunchedEffect(key1 = Unit) {
                empHomeVM.getEmployeeRestaurants()
                empHomeVM.findSelectedRestaurants()
            }


            val startDestination = if (empHomeVM.selectedRestaurant != null)
                EmployeeRoutes.Home
             else
                EmployeeRoutes.SelectRestaurant


            NavHost(navController = innerNavController, startDestination = startDestination, modifier = Modifier.padding(it)){
                composable<EmployeeRoutes.SelectRestaurant>{
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }

                    val restaurants = empHomeVM.restaurants

                    Column {
                        if (restaurants.size == 1) {
                            LaunchedEffect(key1 = Unit) {
                                empHomeVM.selectRestaurant(restaurants.first())
                                innerNavController.navigate(EmployeeRoutes.Home)
                            }
                        }
                        else if (restaurants.size > 1) {
                            Text(
                                text = stringResource(id = R.string.label_employee_greetings, UserService.UserObject.firstName),
                                color = MaterialTheme.colorScheme.background,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(
                                        start = 50.dp,
                                        top = 16.dp,
                                        bottom = 16.dp
                                    )
                                    .background(
                                        MaterialTheme.colorScheme.secondary,
                                        shape = RoundedCornerShape(
                                            topStart = 20.dp,
                                            bottomStart = 20.dp
                                        )
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                            )
                            restaurants.forEach { restaurant ->
                                var img by remember { mutableStateOf<Bitmap?>(null) }
                                LaunchedEffect(key1 = true) {
                                    if (restaurant.logo != null) {
                                        img = empHomeVM.getRestaurantLogo(restaurant)
                                    }
                                }

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    onClick = {
                                        empHomeVM.viewModelScope.launch {
                                            empHomeVM.selectRestaurant(restaurant)
                                            innerNavController.navigate(EmployeeRoutes.Home)
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                        .heightIn(80.dp, 150.dp)
                                ) {
                                    Box() {
                                        if(img!=null){
                                            Image(
                                                bitmap = img!!.asImageBitmap(),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                alpha = 0.2f,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }

                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = restaurant.name+ " - "+ restaurant.restaurantType,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Spacer(modifier = Modifier.height(14.dp))
                                            Text(
                                                text = restaurant.address,
                                                fontSize = 14.sp,
                                            )
                                            if(restaurant.postalIndex.isNotEmpty())
                                                Text(
                                                    text = restaurant.postalIndex,
                                                    fontSize = 14.sp,
                                                )
                                            Text(
                                                text = restaurant.city,
                                                fontSize = 14.sp,
                                            )
                                        }
                                    }
                                }
                            }
                        } else if (empHomeVM.isLoading){
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (empHomeVM.isError) {
                            MissingPage(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(alignment = Alignment.CenterHorizontally)
                                    .padding(vertical = 40.dp),
                                errorStringId = R.string.label_no_restaurants_found
                            )
                        }
                    }
                }

                composable<EmployeeRoutes.Home> {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = true
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MenuButton(
                            stringResource(id = R.string.label_orders),
                            Icons.Outlined.Book)
                        MenuButton(
                            stringResource(id = R.string.label_restaurant_tables),
                            Icons.Outlined.TableBar)
                        MenuButton(
                            stringResource(id = R.string.label_reservations),
                            Icons.Outlined.Inbox)
                        MenuButton(
                            stringResource(id = R.string.label_stock),
                            Icons.Outlined.ShoppingBasket)
                        MenuButton(
                            stringResource(id = R.string.label_orders),
                            Icons.Outlined.Star)
                    }

                }
                composable<MainRoutes.Profile>{
                    SettingsActivity(navController = innerNavController, themeChange = { darkTheme = !darkTheme } )
                }
                composable<AuthRoutes.Landing>{
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    LandingActivity()
                }
            }
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    icon: ImageVector,
    onClick: ()->Unit = {}) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxHeight()
        ) {
            Icon(
                icon,
                contentDescription = text,
                modifier = Modifier.size(50.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.background,
            )
        }
    }
}
