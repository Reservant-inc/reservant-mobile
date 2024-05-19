import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import kotlin.math.floor


@Composable
fun RestaurantDetailActivity(navControllerHome: NavHostController) {

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

        Image(
            painter = painterResource(R.drawable.restaurant_photo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "John Doe’s",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            RatingBar(rating = 3.9f)
            Spacer(modifier = Modifier.width(8.dp))
            Text("3.9 (200+ opinii)")
        }

        Text(
            text = "Koszt dostawy 5,99 zł",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Text(
            text = "Restauracja / Bar\nAdres: ul. Marszałkowska 2, 00-000",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = "Galeria",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            repeat(3) {
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 8.dp)
                )
            }
            TextButton(onClick = { /* TODO: Otwórz galerię */ }) {
                Text("więcej...")
            }
        }

        Text(
            text = "Menu",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(3) { index ->
                MenuTypeButton(
                    modifier = Modifier.scale(1.1f),
                    menuType = "typ menu",
                    onClick = { /* TODO: Handle click */ }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Bedzie forEach
            items(3) { index ->
                MenuCategoryButton(
                    modifier = Modifier.scale(0.9f),
                    category = "Kategoria ${index + 1}",
                    onClick = { /* TODO: Handle click */ },
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Bedzie forEach
        repeat(2) { index ->
            MenuItemCard(
                name = "Nazwa pozycji ${index + 1}",
                price = "Cena: ${10 + index} zł",
                description = "Opis pozycji ${index + 1} (jeśli jest)",
                onEditClick = { /* TODO: Handle edit */ },
                onDeleteClick = { /* TODO: Handle delete */ }
            )
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}


// TODO: Composables to Components.kt ?
@Composable
fun RatingBar(rating: Float) {
    val fullStars = floor(rating).toInt()
    val halfStars = if (rating - fullStars >= 0.5) 1 else 0
    val emptyStars = 5 - fullStars - halfStars

    Row {
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Filled Star"
            )
        }
        repeat(halfStars) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.StarHalf,
                contentDescription = "Half Star"
            )
        }
        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Filled.StarBorder,
                contentDescription = "Empty Star"
            )
        }
    }
}

@Composable
fun MenuTypeButton(modifier: Modifier = Modifier, menuType: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = modifier.padding(4.dp)
    ) {
        Text(menuType)
    }
}

@Composable
fun MenuCategoryButton(modifier: Modifier = Modifier, category: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = modifier.padding(2.dp)
    ) {
        Text(category)
    }
}

@Composable
fun MenuItemCard(name: String, price: String, description: String, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = price,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (description.isNotEmpty()) {
                Text(description, style = MaterialTheme.typography.bodySmall)
            }
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 8.dp)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onDeleteClick) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    }
}
