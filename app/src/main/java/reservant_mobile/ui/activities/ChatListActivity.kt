package reservant_mobile.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.ChatDTO
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.navigation.UserRoutes

@Composable
fun ChatListActivity(navController: NavHostController) {
    val chats = remember {
        listOf(
            ChatDTO("John Doe's staff", "John: What's up?", "10:45 AM"),
            ChatDTO("John Doe", "What's up?", "Yesterday"),
            ChatDTO("John Doe", "What's up?", "12:30 PM"),
            ChatDTO("John Doe", "What's up?", "9:15 AM"),
            ChatDTO("John Doe", "What's up?", "2 days ago")
        )
    }
    val nav = rememberNavController()
    NavHost(
        navController = nav,
        startDestination = UserRoutes.ChatList
    ) {
        composable<UserRoutes.ChatList> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                IconWithHeader(
                    icon = Icons.Rounded.Person,
                    text = "User02",
                    showBackButton = true,
                    onReturnClick = { navController.navigate(MainRoutes.Profile) } //TODO zmienic w przyszłości
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_profile_placeholder),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "John Doe",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar), // Placeholder for calendar icon
                            contentDescription = "Date of Birth",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "01-01-2000")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Rep.: 3.78 / 5.00")
                    }
                }

                // Tabs
                TabRow(
                    selectedTabIndex = 2,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    listOf(
                        "Visits",
                        "Orders",
                        "Chats",
                        "Friends"
                    ).forEachIndexed { index, title ->
                        Tab(
                            selected = index == 2,
                            onClick = { /* Handle tab click */ },
                            text = { Text(title) }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = { /* Handle search query */ },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(text = "Search...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ManageSearch,
                                contentDescription = "Search Icon"
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { /* Handle adding new chat */ },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.GroupAdd,
                            contentDescription = "Add Chat"
                        )
                    }
                }

                LazyColumn {
                    items(chats) { chat ->
                        ChatListItem(chat = chat, onClick = {
                            nav.navigate(UserRoutes.Chat(userName = chat.userName))
                        })
                    }
                }
            }

        }
        composable<UserRoutes.Chat> {
            ChatActivity(
                navController = nav,
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
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_profile_placeholder),
            contentDescription = "Profile Picture",
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