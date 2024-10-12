package reservant_mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reservant_mobile.R
import reservant_mobile.data.services.UserService

@Composable
fun UserCard(){
    Card (
        onClick = {},
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ){
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painterResource(id = R.drawable.jd),
                contentDescription = "placeholder",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp ,end = 16.dp)
                    .size(50.dp)
                    .weight(0.2f)
                    .clip(CircleShape),
            )
            Column(Modifier.weight(0.8f).align(Alignment.CenterVertically)) {
                Text(
                    text = UserService.UserObject.login,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                )
                Text(
                    text = "${UserService.UserObject.firstName} ${UserService.UserObject.lastName}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.W300
                )
            }
        }
    }
}