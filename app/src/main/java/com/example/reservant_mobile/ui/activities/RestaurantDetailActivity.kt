import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R

import com.example.reservant_mobile.ui.components.FloatingTabSwitch
import com.example.reservant_mobile.ui.components.FullscreenGallery
import com.example.reservant_mobile.ui.components.MenuItemCard
import com.example.reservant_mobile.ui.components.RatingBar
import com.example.reservant_mobile.ui.theme.secondaryLight


@Composable
fun RestaurantDetailActivity(navControllerHome: NavHostController) {
    var showGallery by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

        Image(
            painter = painterResource(R.drawable.restaurant_photo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "John Doe’s",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { isFavorite = !isFavorite },
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) secondaryLight else Color.Gray
                )
            }
        }

        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            RatingBar(rating = 3.9f)
            Spacer(modifier = Modifier.width(8.dp))
            Text("3.9 (200+ opinii)")
        }

        Text(
            text = "Restauracja / Bar",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Text(
            text = stringResource(R.string.label_restaurant_address) + ": ul. Marszałkowska 2, 00-000",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = stringResource(R.string.label_delivery_cost) + " 5,99zł",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = stringResource(R.string.label_gallery),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) {
                Card(
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.restaurant_photo),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Card(
                modifier = Modifier
                    .size(100.dp)
                    .clickable { showGallery = true },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f))
                ) {
                    Image(
                        painter = painterResource(R.drawable.restaurant_photo),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.35f
                    )
                    Text(
                        text = stringResource(R.string.label_more),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        FloatingTabSwitch(
            pages = listOf(
                stringResource(R.string.label_menu) to { MenuContent() },
                stringResource(R.string.label_events) to { EventsContent() }
            )
        )
    }

    if (showGallery) {
        FullscreenGallery(onDismiss = { showGallery = false })
    }
}

@Composable
fun MenuContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        repeat(2) { index ->
            MenuItemCard(
                name = "Nazwa pozycji ${index + 1}",
                price = stringResource(R.string.label_menu_price)+ ": 15zl",
                imageResource = R.drawable.pizza,
                description = "Opis pozycji ${index + 1} (jeśli jest)",
                onInfoClick = { /* TODO: Handle info */ },
                onAddClick = { /* TODO: Handle add */ }
            )
        }
    }
}

@Composable
fun EventsContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Text("Wydarzenie 1", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Szczegóły wydarzenia 1")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Wydarzenie 2", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Szczegóły wydarzenia 2")
    }
}