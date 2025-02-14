package reservant_mobile.ui.activities

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import org.osmdroid.views.MapView
import reservant_mobile.ApplicationService
import reservant_mobile.data.constants.PermissionStrings
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.services.NotificationService
import reservant_mobile.data.utils.GetEventsStatus
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.EventCard
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.ImageCard
import reservant_mobile.ui.components.LoadingScreenWithTimeout
import reservant_mobile.ui.components.Logo
import reservant_mobile.ui.components.MessageSheet
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.ui.components.MyDatePickerDialog
import reservant_mobile.ui.components.MyFloatingActionButton
import reservant_mobile.ui.components.NotificationHandler
import reservant_mobile.ui.components.OsmMapView
import reservant_mobile.ui.components.RatingBar
import reservant_mobile.ui.components.RequestPermission
import reservant_mobile.ui.components.RestaurantCard
import reservant_mobile.ui.components.ShowErrorToast
import reservant_mobile.ui.components.SwitchWithLabel
import reservant_mobile.ui.navigation.AuthRoutes
import reservant_mobile.ui.navigation.EventRoutes
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.viewmodels.MapViewModel
import reservant_mobile.ui.viewmodels.RestaurantDetailViewModel
import java.time.LocalDate
import kotlin.math.round
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MapActivity(isUserLoggedIn: Boolean = false){
    val navController = rememberNavController()
    val mapViewModel = viewModel<MapViewModel>()
    NavHost(navController = navController, startDestination = RestaurantRoutes.Map){
        composable<RestaurantRoutes.Map> {

            var showRestaurantBottomSheet by remember { mutableStateOf(false) }
            var showRestaurantId by remember { mutableIntStateOf(0) }
            val restaurants by rememberUpdatedState(newValue = mapViewModel.restaurants.collectAsLazyPagingItems())
            val events by rememberUpdatedState(newValue = mapViewModel.events.collectAsLazyPagingItems())
            var mv:MapView? by remember { mutableStateOf(null) }

            // restaurant filters
            var restaurantSearchQuery by remember { mutableStateOf("") }
            val restaurantSelectedTags = remember { mutableStateListOf<String>() }
            var restaurantSelectedRating by remember { mutableIntStateOf(0) }
            var showRestaurantFiltersSheet by remember { mutableStateOf(false) }

            //events fiters
            var eventSearchQuery by remember { mutableStateOf("") }
            var selectedEventStatus: GetEventsStatus? by remember { mutableStateOf(null) }
            var eventSelectedDateFrom: LocalDate? by remember { mutableStateOf(null) }
            var eventSelectedFriendsOnly: Boolean? by remember { mutableStateOf(null) }
            var eventSelectedDateUntil: LocalDate? by remember { mutableStateOf(null) }
            var showEventFiltersSheet by remember { mutableStateOf(false) }

            val startPoint by remember { mutableStateOf(mapViewModel.userPosition) }

            RequestPermission(
                permission = PermissionStrings.LOCATION,
            )

            if (mv == null) {
                val context = LocalContext.current
                mv = mapViewModel.initMapView(context, startPoint)
            }
            if (showRestaurantBottomSheet) {
                RestaurantDetailPreview(
                    navController = navController,
                    restaurantId = showRestaurantId,
                    onDismiss =  { showRestaurantBottomSheet = false },
                    isUserLoggedIn = isUserLoggedIn
                )
            }


            val pages: List<Pair<String, @Composable () -> Unit>> = listOfNotNull(
                stringResource(id = R.string.label_restaurants) to {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 75.dp, start = 20.dp, end = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            TextField(
                                value = restaurantSearchQuery,
                                onValueChange = {
                                    restaurantSearchQuery = it
                                    mapViewModel.restaurant_search = it
                                    mapViewModel.refreshRestaurants(startPoint)
                                    mapViewModel.refreshEvents(startPoint)
                                },
                                singleLine = true,
                                placeholder = { Text(text = stringResource(id = R.string.label_search)) },
                                modifier = Modifier
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(20.dp),

                                )
                            Spacer(modifier = Modifier.width(16.dp))

                            IconButton(
                                onClick = { showRestaurantFiltersSheet = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filter Icon"
                                )
                            }
                        }

                        // ### PAGINATION EXAMPLE ###
                        if (restaurants.loadState.refresh is LoadState.Loading) {
                            LoadingScreenWithTimeout(timeoutMillis = 20000.milliseconds)
                        } else if(restaurants.itemCount < 1 || restaurants.loadState.hasError){
                            MissingPage(
                                errorString = stringResource(id = R.string.label_no_restaurants_found)
                            )
                        } else {
                            LazyColumn(
                                Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                items(restaurants.itemCount) { index ->
                                    val item = restaurants[index]
                                    if (item != null) {
                                        RestaurantCard(
                                            onClick = {
                                                if(isUserLoggedIn){
                                                    navController.navigate(RestaurantRoutes.Details(restaurantId = item.restaurantId))
                                                } else{
                                                    navController.navigate(AuthRoutes.Login)
                                                }
                                            },
                                            name = item.name,
                                            location = item.address,
                                            city = item.city,
                                            image = item.logo?.asImageBitmap(),
                                            availableHours = item.availableHours
                                        )
                                        mapViewModel.addRestaurantMarker(item) { _, _ ->
                                            showRestaurantId = item.restaurantId
                                            showRestaurantBottomSheet = true
                                            true
                                        }
                                    }
                                }
                            }
                        }
                        // ##########################
                    }
                },
                stringResource(id = R.string.label_events) to {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 75.dp, start = 20.dp, end = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            TextField(
                                value = eventSearchQuery,
                                onValueChange = {
                                    eventSearchQuery = it
                                    mapViewModel.event_search = it
                                    mapViewModel.refreshEvents(startPoint)
                                },
                                singleLine = true,
                                placeholder = { Text(text = stringResource(id = R.string.label_search)) },
                                modifier = Modifier
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(20.dp))
                            Spacer(modifier = Modifier.width(16.dp))

                            IconButton(
                                onClick = { showEventFiltersSheet = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filter Icon"
                                )
                            }
                        }

                        if(events.loadState.refresh is LoadState.Loading){
                            LoadingScreenWithTimeout(timeoutMillis = 10000.milliseconds)
                        }
                        else if (events.itemCount < 1 || events.loadState.hasError
                        ){
                            MissingPage(
                                errorString = stringResource(
                                    id = R.string.message_not_found_any,
                                    stringResource(id = R.string.label_events)
                                )
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                            ) {
                                LazyColumn(
                                    Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    items(events.itemCount) { index ->
                                        val item = events[index]
                                        if(item != null){

                                            var eventPhoto by remember { mutableStateOf<Bitmap?>(null) }

                                            LaunchedEffect(item.photo) {
                                                item.photo?.let { photo ->
                                                    eventPhoto = mapViewModel.getPhoto(photo)
                                                }
                                            }

                                            EventCard(
                                                eventName = item.name,
                                                eventDate = item.time,
                                                eventLocation = if (item.restaurant != null) item.restaurant.address else "",
                                                interestedCount = item.numberInterested,
                                                takePartCount = item.numberParticipants,
                                                eventPhoto = eventPhoto,
                                                onClick = {
                                                    if(isUserLoggedIn){
                                                        navController.navigate(
                                                            EventRoutes.Details(eventId = item.eventId)
                                                        )
                                                    } else {
                                                        navController.navigate(AuthRoutes.Login)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                                if(isUserLoggedIn){
                                    MyFloatingActionButton(
                                        onClick = {
                                            navController.navigate(EventRoutes.AddEvent)
                                        },
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                Pair<String, @Composable () -> Unit>(
                    first = stringResource(id = R.string.label_login_action),
                    second = {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Logo()

                            ButtonComponent(onClick = { navController.navigate(AuthRoutes.Login) }, label = stringResource(
                                id = R.string.label_login_action
                            ))

                            ButtonComponent(onClick = { navController.navigate(AuthRoutes.Register) }, label = stringResource(
                                id = R.string.label_signup
                            ))
                        }
                    }
                ).takeIf { !isUserLoggedIn }
            )

            BottomSheetScaffold(
                scaffoldState = rememberBottomSheetScaffoldState(),
                sheetPeekHeight = 60.dp,
                sheetContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                sheetContent = {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ){
                        FloatingTabSwitch(
                            pages = pages,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                },
                content = { innerPadding -> OsmMapView(mv, startPoint,
                    Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
                },
                contentColor = MaterialTheme.colorScheme.surface
            )

            if(showRestaurantFiltersSheet){
                MessageSheet(
                    height = 450.dp,
                    buttonLabelId = R.string.label_apply,
                    onDismiss = {showRestaurantFiltersSheet = false},
                    buttonOnClick = {
                        mapViewModel.restaurant_selectedTags = restaurantSelectedTags
                        mapViewModel.restaurant_minRating = restaurantSelectedRating
                        mapViewModel.refreshRestaurants(userLocation = startPoint)
                    },
                    content = {
                        mapViewModel.getRestaurantTags()
                        if(mapViewModel.areFiltersLoading){
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        else{
                            val context = ApplicationService.instance
                            val tagList = mapViewModel.restaurantTags

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.label_search_filter_rating),
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                StarRatingFilter(
                                    selectedRating = restaurantSelectedRating,
                                    onRatingSelected = { rating -> restaurantSelectedRating = rating }
                                )

                                HorizontalDivider(Modifier.padding(vertical = 10.dp))

                                Text(
                                    text = stringResource(id = R.string.label_search_filter_tags),
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    tagList.forEach { tag ->
                                        val  errorString = stringResource(id = R.string.error_too_many_tags)

                                        FilterChip(
                                            label = {Text(text = tag)},
                                            selected = restaurantSelectedTags.contains(tag),
                                            onClick = {
                                                if (restaurantSelectedTags.contains(tag)) {
                                                    restaurantSelectedTags.remove(tag)
                                                } else {
                                                    if (restaurantSelectedTags.size < 5) {
                                                        restaurantSelectedTags.add(tag)
                                                    } else {
                                                        Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }

            if(showEventFiltersSheet){
                MessageSheet(
                    buttonLabelId = R.string.label_apply,
                    height = 500.dp,
                    onDismiss = {showEventFiltersSheet = false},
                    buttonOnClick = {
                        mapViewModel.event_status = selectedEventStatus
                        mapViewModel.event_dateFrom = eventSelectedDateFrom
                        mapViewModel.event_dateUntil = eventSelectedDateUntil
                        mapViewModel.event_friendsOnly = eventSelectedFriendsOnly

                        mapViewModel.refreshEvents(userLocation = startPoint)
                    },
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.label_search_filter_event_status),
                                fontSize = 18.sp,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                            EventStatusRadioFilter(
                                selectedStatus = selectedEventStatus,
                                onStatusSelected = {status -> selectedEventStatus = status}
                            )

                            HorizontalDivider(Modifier.padding(vertical = 10.dp))

                            Text(
                                text = stringResource(id = R.string.label_search_filter_date_range),
                                fontSize = 18.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                val dialogWidth = 210.dp
                                val parseString = {date: String ->
                                    try{
                                        LocalDate.parse(date)
                                    } catch (_: Exception){
                                        null
                                    }

                                }
                                MyDatePickerDialog(
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .width(dialogWidth)
                                        .weight(1f),
                                    label = stringResource(id = R.string.label_date_from),
                                    startDate = (eventSelectedDateFrom ?: LocalDate.now()).toString(),
                                    startStringValue = (eventSelectedDateFrom ?: "").toString(),
                                    onDateChange = { date ->
                                        eventSelectedDateFrom = parseString(date)
                                    },
                                    allowFutureDates = true
                                )
                                Text(text = "-")
                                MyDatePickerDialog(
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .width(dialogWidth)
                                        .weight(1f),
                                    label = stringResource(id = R.string.label_date_to),
                                    startDate = (eventSelectedDateUntil ?: LocalDate.now()).toString(),
                                    startStringValue = (eventSelectedDateUntil ?: "").toString(),
                                    onDateChange = { date ->
                                        eventSelectedDateUntil = parseString(date)
                                    },
                                    allowFutureDates = true
                                )
                            }
                            HorizontalDivider(Modifier.padding(vertical = 10.dp))

                            SwitchWithLabel(
                                label = stringResource(id = R.string.label_with_friends_only),
                                checked = eventSelectedFriendsOnly?: false,
                                onCheckedChange = {
                                    eventSelectedFriendsOnly = it
                                }
                            )
                        }
                    }
                )
            }
        }
        composable<RestaurantRoutes.Details> {
            RestaurantDetailActivity(
                restaurantId = it.toRoute<RestaurantRoutes.Details>().restaurantId,
                onReturnClick = { navController.popBackStack() }
            )
        }
        composable<EventRoutes.Details>{
            EventDetailActivity(
                navController = navController,
                eventId = it.toRoute<EventRoutes.Details>().eventId
            )
        }
        composable<EventRoutes.AddEvent>{
            AddEventActivity(navController = navController)
        }
        composable<AuthRoutes.Login>{
            LandingActivity(
                startDestination = AuthRoutes.Login,
                onReturnClick = {navController.popBackStack()}
            )
        }
        composable<AuthRoutes.Register>{
            LandingActivity(
                startDestination = AuthRoutes.Register,
                onReturnClick = {navController.popBackStack()}
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailPreview(
    navController: NavHostController,
    restaurantId: Int,
    onDismiss: () -> Unit,
    isUserLoggedIn: Boolean = false
){
    val modalBottomSheetState = rememberModalBottomSheetState()

    val restaurantDetailVM = viewModel<RestaurantDetailViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                RestaurantDetailViewModel(restaurantId) as T
        }
    )

    LaunchedEffect(key1 = true) {
        restaurantDetailVM.loadRestaurant(restaurantId)
    }
    val contentHeight = if (restaurantDetailVM.restaurant?.photos != null && restaurantDetailVM.restaurant!!.photos.isNotEmpty())
        450.dp
    else
        350.dp

    ModalBottomSheet(
        onDismissRequest = { onDismiss()},
        sheetState = modalBottomSheetState,
        modifier = Modifier.height(contentHeight)
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            if (restaurantDetailVM.resultRestaurant.isError) {
                ShowErrorToast(context = LocalContext.current, id = restaurantDetailVM.getToastError())
            }

            when {
                restaurantDetailVM.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                restaurantDetailVM.restaurant != null -> {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        restaurantDetailVM.restaurant?.let { restaurant ->

                            if(restaurant.photos.isNotEmpty()){
                                var images by remember { mutableStateOf<List<Bitmap>>(emptyList()) }

                                LaunchedEffect(restaurant.photos) {
                                    val loadedImages = restaurantDetailVM.getPhotos(restaurant.photos, limit = 6)
                                    images = loadedImages
                                }

                                if (restaurantDetailVM.isGalleryLoading){
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                                else {
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        images.forEach { img ->
                                            Box(
                                                modifier = Modifier.padding(horizontal = 8.dp)
                                            ) {
                                                ImageCard(
                                                    image = img.asImageBitmap()
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.width(220.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = restaurant.name,
                                        style = MaterialTheme.typography.headlineSmall,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )

                                    Text(
                                        text = restaurant.restaurantType,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
                                    )

                                    Row(
                                        modifier = Modifier.padding(bottom = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        Icon(
                                            modifier = Modifier
                                                .padding(start = 16.dp),
                                            imageVector = Icons.Rounded.LocationOn,
                                            contentDescription = "Restaurant location"

                                        )
                                        Text(
                                            text = "${restaurant.city}\n${restaurant.address}",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }

                                    if(restaurant.provideDelivery){
                                        Text(
                                            text = stringResource(R.string.label_delivery_cost) + ": 5,70zł",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                    }
                                }
                                if(restaurant.rating != null && restaurant.numberReviews!! > 0){
                                    val rating = restaurant.rating.toFloat()
                                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                        RatingBar(rating = rating)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("${String.format("%.1f", rating)} (${restaurant.numberReviews} opinii)")
                                    }
                                }
                            }

                                ButtonComponent(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .wrapContentHeight(align = Alignment.CenterVertically),
                                    onClick = {
                                        onDismiss()
                                        if(isUserLoggedIn){
                                            navController.navigate(RestaurantRoutes.Details(restaurantId =  restaurant.restaurantId))
                                        } else{
                                            navController.navigate(AuthRoutes.Login)
                                        }

                                    },

                                    label = stringResource(id = R.string.label_show_more_details)
                                )
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        MissingPage(errorStringId = R.string.error_not_found)
                    }
                }
            }
        }
    }
}

@Composable
fun StarRatingFilter(
    selectedRating: Int,
    onRatingSelected: (Int) -> Unit
) {
    val selectedColor = MaterialTheme.colorScheme.primary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        for (star in 1..5) {
            Icon(
                imageVector = if (star <= selectedRating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = selectedColor,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(32.dp)
                    .clickable {
                        onRatingSelected(star)
                    }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EventStatusRadioFilter(
    selectedStatus: GetEventsStatus?,
    onStatusSelected: (GetEventsStatus?) -> Unit
) {
    var currentStatus by remember { mutableStateOf(selectedStatus) }
    val selectStatus = {status:GetEventsStatus? ->
        currentStatus = status
        onStatusSelected(status)
    }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    )  {
        GetEventsStatus.entries.forEach { status ->
            val statusValue = if(status == GetEventsStatus.All) null else status
            FilterChip(
                label = {Text(text = stringResource(id = status.stringId))},
                selected = currentStatus == statusValue,
                onClick = {
                    selectStatus(statusValue)
                }
            )
        }
    }
}