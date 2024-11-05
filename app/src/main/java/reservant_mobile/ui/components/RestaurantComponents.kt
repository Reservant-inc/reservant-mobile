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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentCompositionErrors
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.reservant_mobile.R
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.RestaurantMenuDTO
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.data.utils.getRestaurantOpeningTime
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjuster
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
fun EventsContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 80.dp, end = 16.dp, bottom = 16.dp)
    ) {
        repeat(3) {
            EventCard(
                eventCreator = "Name of event",
                eventDate = "Saturday, 2024-06-22",
                eventLocation = "John's Doe - Warsaw",
                interestedCount = 20,
                takePartCount = 45
            )
            Modifier.padding(bottom = 16.dp)
        }
    }
}

@Composable
fun EventCard(
    eventCreator: String,
    eventDate: String,
    eventLocation: String,
    interestedCount: Int,
    takePartCount: Int
) {
    val date = formatToDateTime(eventDate, "dd MMMM yyyy")
    val time = formatToDateTime(eventDate, "HH:mm")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
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
                    Image(
                        painter = painterResource(id = R.drawable.restaurant_photo),
                        contentDescription = "Event Image",
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }

                Text(
                    text = "$time | $date",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = eventLocation,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = eventCreator,
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
    getMenuPhoto: suspend (String) -> Bitmap?
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

            LaunchedEffect(menuItem.photo) {
                menuItem.photo?.let { photo ->
                    menuPhoto = getMenuPhoto(photo)
                }
            }

            MenuItemCard(
                menuItem = menuItem,
                role = Roles.CUSTOMER,
                photo = menuPhoto,
                onInfoClick = { /* TODO: Handle info */ },
                onAddClick = { /* TODO: Handle add */ }
            )
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
            modifier = Modifier.animateContentSize(),
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
                            }
                        } else {
                            append("${availableHours.from} - ${availableHours.until}\n")
                        }


                    }

                } else {
                    pushStyle(SpanStyle(color = if (isOpen) Color.Green else Color.Red))
                    append(stringResource(id = if (isOpen) R.string.label_open else R.string.label_closed ))
                    pop()
                    append(" • $openingTime - $closingTime")
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