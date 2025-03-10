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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.ChatDTO

@Composable
fun UserCard(
    firstName: String?,
    lastName: String?,
    getImage: suspend () -> Bitmap?,
    onClick: () -> Unit = { },
    // New:
    isDeletable: Boolean = false,
    onRemove: (() -> Unit)? = null
) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            // Photo section
            LoadedPhotoComponent(
                photoModifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp, end = 16.dp)
                    .size(50.dp)
                    .weight(0.2f)
                    .clip(CircleShape),
                placeholderModifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp, end = 16.dp)
                    .size(50.dp)
                    .weight(0.2f)
                    .clip(CircleShape),
                getPhoto = getImage,
                placeholder = R.drawable.ic_profile_placeholder
            )

            Column(
                Modifier
                    .weight(if (isDeletable) 0.7f else 0.8f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "${firstName ?: ""} ${lastName ?: ""}".trim(),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                )
            }

            if (isDeletable && onRemove != null) {
                androidx.compose.material3.IconButton(
                    onClick = { onRemove() },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.remove),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}


@Composable
fun ThreadListItem(
    title: String,
    userNames: String? = null,
    onClick: () -> Unit,
    getPhoto: suspend () -> Bitmap?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LoadedPhotoComponent(
            placeholderModifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            photoModifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            placeholder = R.drawable.ic_profile_placeholder,
            contentScale = ContentScale.Crop,
            getPhoto = getPhoto
        )

        Column(modifier = Modifier.weight(1f).padding(start = 16.dp, end = 8.dp)) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            userNames?.let {
                Text(text = userNames, fontSize = 14.sp)
            }
        }

    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}