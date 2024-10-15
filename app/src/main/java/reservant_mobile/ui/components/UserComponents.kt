package reservant_mobile.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import reservant_mobile.data.models.dtos.ChatDTO
import reservant_mobile.data.services.UserService

@Composable
fun UserCard(
    firstName: String?,
    lastName: String?,
    getImage: suspend () -> Bitmap?,
){
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
            LoadedPhotoComponent(
                photoModifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp, end = 16.dp)
                    .size(50.dp)
                    .weight(0.2f)
                    .clip(CircleShape),
                getPhoto = getImage
            )

            Column(Modifier.weight(0.8f).align(Alignment.CenterVertically)) {
                Text(
                    text = "$firstName $lastName",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                )
            }
        }
    }
}

@Composable
fun ChatListItem(chat: ChatDTO, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_profile_placeholder),
            contentDescription = "Settings Picture",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = chat.userName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = chat.lastMessage, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = chat.timeStamp,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}