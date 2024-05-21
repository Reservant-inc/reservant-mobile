import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.MenuCategoryButton
import com.example.reservant_mobile.ui.components.MenuItemCard
import com.example.reservant_mobile.ui.components.MenuTypeButton
import com.example.reservant_mobile.ui.components.RatingBar


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
                    .size(100.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f))
                        .clickable {
                            println("Card clicked")
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.restaurant_photo),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.35f
                    )
                    Text(
                        text = "Więcej",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
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
            items(3) { index ->
                MenuCategoryButton(
                    modifier = Modifier.scale(0.9f),
                    category = "Kategoria ${index + 1}",
                    onClick = { /* TODO: Handle click */ },
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

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