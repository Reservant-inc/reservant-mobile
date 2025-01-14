@file:Suppress("IMPLICIT_CAST_TO_ANY")

package reservant_mobile.ui.activities

import WarehouseActivity
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.TableBar
import androidx.compose.material.icons.rounded.RestaurantMenu
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import androidx.navigation.toRoute
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.PrefsKeys
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.constants.ThemePrefsKeys
import reservant_mobile.data.models.dtos.IngredientDTO
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.BottomNavItem
import reservant_mobile.data.utils.toCustomNavType
import reservant_mobile.ui.components.BottomNavigation
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.ui.navigation.AuthRoutes
import reservant_mobile.ui.navigation.EmployeeRoutes
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.theme.AppTheme
import reservant_mobile.ui.viewmodels.EmployeeHomeViewModel
import kotlin.reflect.typeOf

@Composable
fun EmployeeHomeActivity() {
    val empHomeVM = viewModel<EmployeeHomeViewModel>()
    val innerNavController = rememberNavController()


    val isSystemInDarkMode = isSystemInDarkTheme()
    var darkTheme by remember {
        mutableStateOf(isSystemInDarkMode)
    }
    LaunchedEffect(key1 = Unit) {
        val tmp = empHomeVM.localDataService.getData(PrefsKeys.APP_THEME)
        darkTheme = if(tmp.isEmpty()) isSystemInDarkMode else tmp == ThemePrefsKeys.DARK.themeValue
    }

    val items = listOfNotNull(
        BottomNavItem.Employee,
        BottomNavItem.Profile
    )

    AppTheme(darkTheme = darkTheme) {
        Scaffold(
            bottomBar = {
                BottomNavigation(
                    navController = innerNavController,
                    bottomBarState = remember { (mutableStateOf(false)) },
                    items = items
                )
            }
        ) {
            LaunchedEffect(key1 = Unit) {
                empHomeVM.getEmployeeRestaurants()
                empHomeVM.findSelectedRestaurants()
            }

            NavHost(
                navController = innerNavController,
                startDestination = EmployeeRoutes.SelectRestaurant,
                modifier = Modifier.padding(it)
            ) {
                composable<EmployeeRoutes.SelectRestaurant> {

                    val restaurants = empHomeVM.restaurants

                    Column {
                        if (restaurants.size == 1) {
                            LaunchedEffect(key1 = Unit) {
                                empHomeVM.selectRestaurant(restaurants.first())
                                innerNavController.navigate(EmployeeRoutes.Home)
                            }
                        } else if (restaurants.size > 1) {
                            Text(
                                text = stringResource(
                                    id = R.string.label_employee_greetings,
                                    UserService.UserObject.firstName
                                ),
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
                                        if (img != null) {
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
                                                text = restaurant.name + " - " + restaurant.restaurantType,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Spacer(modifier = Modifier.height(14.dp))
                                            Text(
                                                text = restaurant.address,
                                                fontSize = 14.sp,
                                            )
                                            if (restaurant.postalIndex.isNotEmpty())
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
                        } else if (empHomeVM.isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (empHomeVM.isError) {
                            Column(modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)) {
                                MissingPage(
                                    modifier = Modifier
                                        .align(alignment = Alignment.CenterHorizontally)
                                        .padding(vertical = 40.dp)
                                        .weight(3f),
                                    errorStringId = R.string.label_no_restaurants_found
                                )
                                EmpMenuButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(190.dp)
                                        .weight(1f),
                                    option = EmpMenuOption(
                                        text = stringResource(id = R.string.label_settings),
                                        icon = Icons.Outlined.Settings,
                                        onClick = { innerNavController.navigate(MainRoutes.Settings)}
                                    )
                                )
                            }
                        }
                    }
                }

                composable<EmployeeRoutes.Home> {
                    val restaurant = empHomeVM.selectedRestaurant!!
                    val options: List<EmpMenuOption> = listOf(
                        EmpMenuOption(
                            text = stringResource(id = R.string.label_orders),
                            icon = Icons.Outlined.Book,
                            background = painterResource(id = R.drawable.people_restaurant),
                            onClick = {
                                innerNavController.navigate(
                                    RestaurantRoutes.ManageOrders(
                                        restaurantId = restaurant.restaurantId
                                    )
                                )
                            }
                        ),
                        EmpMenuOption(
                            text = stringResource(id = R.string.label_restaurant_tables),
                            icon = Icons.Outlined.TableBar,
                            background = painterResource(id = R.drawable.table_cafe_town_restaurant),
                            onClick = {
                                innerNavController.navigate(
                                    RestaurantRoutes.Tables(
                                        restaurantId = restaurant.restaurantId
                                    )
                                )
                            }
                        ),
                        EmpMenuOption(
                            text = stringResource(id = R.string.label_reservations),
                            icon = Icons.Outlined.Inbox,
                            background = painterResource(id = R.drawable.reservation_checklist),
                            onClick = {
                                innerNavController.navigate(
                                    RestaurantRoutes.Reservation(
                                        restaurantId = restaurant.restaurantId,
                                        isReservation = true
                                    )
                                )
                            }
                        ),
                        EmpMenuOption(
                            text = stringResource(id = R.string.label_warehouse),
                            icon = Icons.Outlined.ShoppingBasket,
                            background = painterResource(id = R.drawable.wood_wine_store),
                            onClick = {
                                innerNavController.navigate(
                                    RestaurantRoutes.Warehouse(
                                        restaurantId = restaurant.restaurantId
                                    )
                                )
                            }
                        ),
                        EmpMenuOption(
                            text = stringResource(id = R.string.label_reviews),
                            icon = Icons.Outlined.Star,
                            background = painterResource(id = R.drawable.reviews_photo),
                            onClick = {
                                innerNavController.navigate(
                                    RestaurantRoutes.Reviews(
                                        restaurantId = restaurant.restaurantId
                                    )
                                )
                            }
                        ),
                        EmpMenuOption(
                            text = stringResource(id = R.string.label_settings),
                            icon = Icons.Outlined.Settings,
                            onClick = { innerNavController.navigate(MainRoutes.Settings) }
                        ),
                    )


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (restaurant != null)
                            IconWithHeader(
                                icon = Icons.Rounded.RestaurantMenu,
                                text = restaurant.name,
                                showBackButton = empHomeVM.restaurants.size > 1,
                                onReturnClick = {
                                    innerNavController.navigate(EmployeeRoutes.SelectRestaurant)
                                }
                            )
                        LazyVerticalGrid(
                            modifier = Modifier
                                .heightIn(200.dp, 1000.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            columns = GridCells.Adaptive(minSize = 200.dp)
                        ) {
                            items(options.size) { optionIndex ->
                                val option = options[optionIndex]
                                EmpMenuButton(option = option)
                            }
                        }
                    }
                }
                composable<MainRoutes.Settings> {
                    SettingsActivity(
                        homeNavController = innerNavController,
                        themeChange = {
                            darkTheme = !darkTheme
                            empHomeVM.viewModelScope.launch {
                                val tmp = if(darkTheme) ThemePrefsKeys.DARK else ThemePrefsKeys.LIGHT
                                empHomeVM.localDataService.saveData(PrefsKeys.APP_THEME, tmp.themeValue)
                            }
                                      },
                        withBackButton = true
                    )
                }
                composable<AuthRoutes.Landing> {
                    LandingActivity()
                }
                composable<RestaurantRoutes.ManageOrders> {
                    OrderManagementScreen(
                        onReturnClick = { innerNavController.popBackStack() },
                        restaurantId = it.toRoute<RestaurantRoutes.Details>().restaurantId,
                        isReservation = false,
                        navHostController = innerNavController
                    )
                }
                composable<RestaurantRoutes.Reservation> {
                    OrderManagementScreen(
                        onReturnClick = { innerNavController.popBackStack() },
                        restaurantId = it.toRoute<RestaurantRoutes.Reservation>().restaurantId,
                        isReservation = true,
                        navHostController = innerNavController
                    )
                }
                composable<RestaurantRoutes.Tables> {
                    EmployeeTablesActivity(
                        onReturnClick = { innerNavController.popBackStack() },
                        restaurantId = it.toRoute<RestaurantRoutes.Tables>().restaurantId
                    )
                }
                composable<RestaurantRoutes.Warehouse> {
                    WarehouseActivity(
                        onReturnClick = { innerNavController.popBackStack() },
                        restaurantId = it.toRoute<RestaurantRoutes.Warehouse>().restaurantId,
                        isEmployee = Roles.RESTAURANT_EMPLOYEE in UserService.UserObject.roles,
                        navHostController = innerNavController
                    )
                }
                composable<RestaurantRoutes.Reviews> {
                    ReviewsActivity(
                        navController = innerNavController,
                        restaurantId = it.toRoute<RestaurantRoutes.Reviews>().restaurantId,
                        isOwner = false
                    )
                }

                composable<RestaurantRoutes.IngredientHistory>(
                    typeMap = mapOf(typeOf<IngredientDTO>() to toCustomNavType(IngredientDTO.serializer())),
                ) {
                    val item = it.toRoute<RestaurantRoutes.IngredientHistory>().ingredient
                    IngredientDetailsActivity(onReturnClick = { innerNavController.popBackStack() }, ingredient = item)
                }
            }
        }
    }
}

data class EmpMenuOption(
    val text: String,
    val background: Painter? = null,
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)

@Composable
fun EmpMenuButton(
    modifier: Modifier = Modifier
        .padding(8.dp)
        .size(190.dp),
    option: EmpMenuOption
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        onClick = option.onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            if (option.background != null) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = option.background,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = 0.25F
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = option.text,
                    modifier = Modifier.size(50.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = option.text,
                    fontSize = 25.sp,
                    color = MaterialTheme.colorScheme.background,
                )
            }
        }
    }
}
