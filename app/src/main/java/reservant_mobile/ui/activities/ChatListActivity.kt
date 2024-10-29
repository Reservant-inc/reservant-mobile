package reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.endpoints.User
import reservant_mobile.ui.components.ThreadListItem
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.ui.components.MyFloatingActionButton
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.navigation.UserRoutes
import reservant_mobile.ui.viewmodels.SocialViewModel

@Composable
fun ChatListActivity() {
    val viewmodel = viewModel<SocialViewModel>()
    val threads by rememberUpdatedState(viewmodel.threads.collectAsLazyPagingItems())
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
                        viewmodel.threadQuery
                    }

                    OutlinedTextField(
                        value = query,
                        onValueChange = {
                            query = it
                            viewmodel.viewModelScope.launch {
                                viewmodel.getThreads(query)
                            }
                        },
                        placeholder = { Text(text = "Search...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        trailingIcon = {
                            IconButton(onClick = {}){
                                Icon(imageVector = Icons.Rounded.Search, contentDescription = "Send")
                            }
                        },
                        singleLine = true
                    )
                }

                LazyColumn {
                    if (threads.loadState.refresh is LoadState.Loading){
                        item {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                    } else if (threads.loadState.hasError || threads.itemCount < 1) {
                        item {
                            MissingPage(
                                errorString = stringResource(
                                    id = R.string.error_threads_not_found
                                )
                            )
                        }
                    } else {
                        items(threads.itemCount) { i ->
                            val thread by remember {
                                mutableStateOf(threads[i])
                            }

                            thread?.let { thread ->

                                val title by remember {
                                    mutableStateOf(
                                        thread.title ?: thread.participants!!.joinToString(separator = ", ") { it.firstName }
                                    )
                                }

                                val usernames by remember {
                                    mutableStateOf(
                                        thread.participants!!.joinToString(separator = ", ") { it.firstName }
                                    )
                                }

                                ThreadListItem(
                                    title = title,
                                    userNames = usernames,
                                    onClick = {
                                        nav.navigate(UserRoutes.Chat(threadId = thread.threadId!!, threadTitle = title))
                                    }
                                )
                            }
                        }
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
                        nav.navigate(UserRoutes.FindFriends)
                    },
                    icon = Icons.Rounded.PersonAdd
                )
            }

        }
        composable<UserRoutes.Chat> {
            ChatActivity(
                navController = nav,
                threadId = it.toRoute<UserRoutes.Chat>().threadId,
                title = it.toRoute<UserRoutes.Chat>().threadTitle
            )
        }
        composable<UserRoutes.FindFriends> {
            FindFriendsActivity(navController = nav)
        }
    }
}

