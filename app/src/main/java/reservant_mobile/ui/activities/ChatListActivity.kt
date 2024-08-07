package reservant_mobile.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.ChatDTO
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.navigation.UserRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListActivity() {
    val chats = remember {
        listOf(
            ChatDTO("John Doe", "Hey, how are you?", "10:00"),
            ChatDTO("Jane Smith", "Let's meet tomorrow", "10:01"),
            ChatDTO("Alice Johnson", "Did you finish the project?", "10:01"),
            ChatDTO("Bob Brown", "What's up?", "10:02")
        )
    }
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = UserRoutes.ChatList
    ) {
        composable<UserRoutes.ChatList> {
            Column(modifier = Modifier.padding(16.dp)) {
                IconWithHeader(
                    icon = Icons.AutoMirrored.Rounded.Chat,
                    text = stringResource(id = R.string.label_chats),
                )

                LazyColumn {
                    items(chats) { chat ->
                        ChatListItem(chat = chat, onClick = {
                            navController.navigate(UserRoutes.Chat(userName = chat.userName))
                        })
                    }
                }
            }
        }
        composable<UserRoutes.Chat> {
            ChatActivity(
                navController = navController,
                userName = it.toRoute<UserRoutes.Chat>().userName,
            )
        }
    }
}

@Composable
fun ChatListItem(chat: ChatDTO, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        ) {
            // Placeholder for user icon
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = chat.userName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = chat.lastMessage,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = chat.timeStamp,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}
