package reservant_mobile.ui.components

import android.graphics.Bitmap
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.reservant_mobile.R
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.models.dtos.RestaurantMenuDTO
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.data.utils.formatDateTime

@Composable
fun RestaurantCard(
    onClick: () -> Unit,
    name: String,
    location: String,
    city: String,
    image: ImageBitmap?
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
                    .padding(16.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$location, $city",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
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
    takePartCount: Int,
    eventName: String? = null
) {
    val date = formatDateTime(eventDate, "dd MMMM yyyy")
    val time = formatDateTime(eventDate, "HH:mm")
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
                        contentScale = ContentScale.Crop,
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