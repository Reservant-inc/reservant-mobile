package reservant_mobile.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.reservant_mobile.R
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.RestaurantMenuDTO
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.data.utils.getRestaurantOpeningTime
import reservant_mobile.ui.navigation.EventRoutes
import reservant_mobile.ui.viewmodels.RestaurantDetailViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun RestaurantCard(
    onClick: () -> Unit,
    name: String,
    location: String,
    city: String,
    image: ImageBitmap?,
    availableHours: List<RestaurantDTO.AvailableHours>?
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
        ) {
            if(image != null){
                Image(
                    bitmap = image,
                    contentDescription = null,
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            }
            else {
                Image(
                    painter = painterResource(R.drawable.restaurant_template_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)

            ) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = "$location, $city",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                availableHours?.let {

                    var openingTime by remember {
                        mutableStateOf(it.getRestaurantOpeningTime())
                    }

                    var closingTime by remember {
                        mutableStateOf(it.getRestaurantOpeningTime(opening = false))
                    }

                    var currentTime by remember {
                        mutableStateOf(LocalTime.now())
                    }

                    val isOpen = openingTime != null && closingTime != null && currentTime > openingTime && currentTime < closingTime

                    closingTime?.let {
                        if (isOpen){
                            val isNearClosing = closingTime!!.minusHours(1) < currentTime

                            if (isNearClosing) {
                                Text(
                                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                                    text = "${stringResource(id = R.string.label_closing_soon)}: $it",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Text(
                                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                                    text = "${stringResource(id = R.string.label_closing_at)}: $it",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                        } else {
                            Text(
                                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                                text = "${stringResource(id = R.string.label_closed)}: $it",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )

                        }
                    }

                }
            }
        }
    }
}

@Composable
fun EventCard(
    eventName: String,
    eventDate: String,
    eventLocation: String,
    interestedCount: Int,
    takePartCount: Int,
    eventPhoto: Bitmap?,
    onClick: () -> Unit
) {
    val date = formatToDateTime(eventDate, "dd MMMM yyyy")
    val time = formatToDateTime(eventDate, "HH:mm")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(bottom = 16.dp)
                ) {
                    if(eventPhoto != null){
                        Image(
                            bitmap = eventPhoto.asImageBitmap(),
                            contentScale = ContentScale.Crop,
                            contentDescription = "Event Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                        )
                    }else{
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                }

                Text(
                    text = "$time | $date",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = eventName,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = eventLocation,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if(interestedCount != 0)
                        Text(
                            text = "$interestedCount "+stringResource(R.string.label_interested)
                        )
                    if(takePartCount != 0)
                        Text(
                            text = "$takePartCount "+stringResource(R.string.label_takePart)
                        )
                    if(interestedCount == 0 && takePartCount == 0)
                        Text(
                            text = stringResource(R.string.label_no_people_yet)
                        )
                }
            }

        }
    }
}

@Composable
fun MenuContent(
    menus: List<RestaurantMenuDTO>,
    menuItems: List<RestaurantMenuItemDTO>?,
    onMenuClick: (Int) -> Unit,
    getMenuPhoto: suspend (String) -> Bitmap?,
    onAddClick: (RestaurantMenuItemDTO) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            for (menu in menus) {
                MenuTypeButton(
                    menuType = menu.name,
                    onMenuClick = { menu.menuId?.let { onMenuClick(it) } }
                )
            }
        }

        menuItems?.forEach { menuItem ->
            var menuPhoto by remember { mutableStateOf<Bitmap?>(null) }

            MenuItemCard(
                menuItem = menuItem,
                role = Roles.CUSTOMER,
                getPhoto = {
                    menuItem.photo?.let {
                        getMenuPhoto(it)
                    }
                },
                onInfoClick = { /* TODO: Handle info */ },
                onAddClick = {
                    onAddClick(menuItem)
                }
            )
        }

    }
}

@Composable
fun EventsContent(
    eventsFlow: LazyPagingItems<EventDTO>?,
    restaurantDetailVM: RestaurantDetailViewModel,
    navController: NavController
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        eventsFlow?.let { events ->
            if (events.itemCount == 0 && events.loadState.refresh !is LoadState.Loading) {
                Text(text = stringResource(R.string.label_restaurant_no_events))
            } else {
                Column {
                    for (index in 0 until events.itemCount) {
                        val event = events[index]
                        if (event != null) {

                            var eventPhoto by remember { mutableStateOf<Bitmap?>(null) }

                            LaunchedEffect(event.photo) {
                                event.photo?.let { photo ->
                                    eventPhoto = restaurantDetailVM.getPhoto(photo)
                                }
                            }

                            EventCard(
                                eventName = event.name.orEmpty(),
                                eventDate = event.time,
                                eventLocation = event.restaurant?.address.orEmpty(),
                                interestedCount = event.numberInterested ?: 0,
                                takePartCount = event.numberParticipants ?: 0,
                                eventPhoto = eventPhoto,
                                onClick = {
                                    navController.navigate(
                                        EventRoutes.Details(
                                            eventId = event.eventId ?: return@EventCard
                                        )
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    when (events.loadState.append) {
                        is LoadState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        is LoadState.Error -> {
                            MissingPage()
                        }
                        else -> Unit
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.label_loading_reviews))
            }
        }
    }
}

@Composable
fun OpeningHours(
    openingHours: List<RestaurantDTO.AvailableHours>
){

    var isExpanded by remember {
        mutableStateOf(false)
    }

    val openingTime by remember {
        mutableStateOf(openingHours.getRestaurantOpeningTime())
    }
    
    val closingTime by remember {
        mutableStateOf(openingHours.getRestaurantOpeningTime(opening = false))
    }

    val currentTime by remember {
        mutableStateOf(LocalTime.now())
    }

    val currentDay by remember {
        mutableIntStateOf(LocalDate.now().dayOfWeek.value - 1)
    }
    
    val isOpen = openingTime != null && closingTime != null && currentTime > openingTime && currentTime < closingTime

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            modifier = Modifier
                .animateContentSize(),
            text = buildAnnotatedString {
                if (isExpanded) {

                    openingHours.forEachIndexed { index, availableHours ->

                        val date by remember {
                            mutableStateOf(
                                LocalDate.now()
                                    .with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
                                    .plusDays(index.toLong())
                            )
                        }

                        if (index == currentDay) {
                            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        }

                        append(date.format(DateTimeFormatter.ofPattern("E", Locale.getDefault())))

                        if (index == currentDay) {
                            pop()
                        }

                        append(" • ")

                        if (availableHours.from == null && availableHours.until == null){
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)){
                                append(stringResource(id = R.string.label_closed))
                                append("\n")
                            }
                        } else {
                            append("${availableHours.from} - ${availableHours.until}\n")
                        }


                    }

                } else {
                    pushStyle(SpanStyle(color = if (isOpen) Color.Green else Color.Red))
                    append(stringResource(id = if (isOpen) R.string.label_open else R.string.label_closed ))
                    pop()
                    if (openingTime != null && closingTime != null){
                        append(" • $openingTime - $closingTime\n")
                    }
                }
            },
            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
        )
        Icon(
            Icons.Rounded.KeyboardArrowDown,
            contentDescription = "Expand icon",
            modifier = Modifier
                .clip(CircleShape)
                .padding(horizontal = 4.dp)
                .rotate(if (isExpanded) 180f else 0f)
                .clickable {
                    isExpanded = !isExpanded
                }
        )

    }
}

@Composable
fun OpeningHourDayInput(
    dayOfWeek: String,
    isOpen: Boolean,
    onOpenChange: (Boolean) -> Unit,
    startTime: String,
    onStartTimeChange: (String) -> Unit,
    endTime: String,
    onEndTimeChange: (String) -> Unit,
){
    val mod = if (isOpen) Modifier else Modifier.background(Color.Gray)

    Row (
        modifier = mod
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .align(Alignment.CenterVertically)
        ){
            Checkbox(
                modifier = Modifier.align(Alignment.CenterVertically),
                checked = !isOpen,
                onCheckedChange = onOpenChange
            )
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = dayOfWeek,
                textDecoration = if (isOpen) TextDecoration.None else TextDecoration.LineThrough
            )
        }


        if (isOpen) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                MyTimePickerDialog(
                    initialTime = startTime,
                    onTimeSelected = onStartTimeChange,
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(end = 4.dp)
                )
                MyTimePickerDialog(
                    initialTime = endTime,
                    onTimeSelected = onEndTimeChange,
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = 4.dp)
                )
            }

        } else {
            Column(
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    text = "Closed",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}