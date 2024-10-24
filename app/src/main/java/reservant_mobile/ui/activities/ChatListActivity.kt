package reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.ChatDTO
import reservant_mobile.ui.components.ChatListItem
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.MyFloatingActionButton
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.navigation.UserRoutes

@Composable
fun ChatListActivity() {
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
                    icon = Icons.AutoMirrored.Rounded.Chat,
                    text = stringResource(id = R.string.label_chats)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    var query by remember {
                        mutableStateOf("")
                    }

                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text(text = "Search...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    //reload items from backend
                                }
                            ){
                                Icon(imageVector = Icons.Rounded.Search, contentDescription = "Send")
                            }
                        },
                        singleLine = true
                    )
                }

                LazyColumn {
                    items(chats) { chat ->
                        ChatListItem(chat = chat, onClick = {
                            nav.navigate(UserRoutes.Chat(userName = chat.userName))
                        })
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                MyFloatingActionButton(
                    onClick = {
                        nav.navigate(MainRoutes.Social)
                    },
                    icon = Icons.Rounded.Search
                )
            }

        }
        composable<UserRoutes.Chat> {
            ChatActivity(
                navController = nav,
                userName = it.toRoute<UserRoutes.Chat>().userName,
            )
        }
        composable<MainRoutes.Social> {
            SocialActivity(navController = nav)
        }
    }
}

