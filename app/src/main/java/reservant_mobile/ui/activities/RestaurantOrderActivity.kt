package reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RestaurantOrderActivity(navController: NavController) {

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 4.dp)) {
        Text(text = "test")
    }

}