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
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import reservant_mobile.data.models.dtos.ChatDTO
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
                        mutableStateOf(viewmodel.threadQuery)
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
                                    viewmodel.viewModelScope.launch {
                                        viewmodel.getThreads(query)
                                    }
                                }
                            ){
                                Icon(imageVector = Icons.Rounded.Search, contentDescription = "Send")
                            }
                        },
                        singleLine = true
                    )
                }

                LazyColumn {

                    if (threads.loadState.refresh is LoadState.Loading){
                        item {
                            CircularProgressIndicator()
                        }
                    }

                    if (threads.loadState.hasError || threads.itemCount < 1) {
                        item {
                            MissingPage(
                                errorString = stringResource(
                                    id = R.string.error_threads_not_found
                                )
                            )
                        }
                    }

                    items(threads.itemCount) { i ->
                        val thread by remember {
                            mutableStateOf(threads[i])
                        }

                        thread?.let { thread ->

                            //TODO: check if thread is group thread
                            val isGroup by remember {
                                mutableStateOf(i==1)
                            }

                            val title by remember {
                                mutableStateOf(
                                    if (isGroup) {
                                        thread.title ?: thread.participants!!.joinToString { "${it.firstName}," }
                                    }
                                    else {
                                        thread.participants!![0].firstName
                                    }
                                )
                            }

                            val usernames by remember {
                                mutableStateOf(
                                    if (isGroup) {
                                        thread.participants!!.joinToString { "${it.firstName}," }
                                    }
                                    else {
                                        null
                                    }
                                )
                            }

                            ThreadListItem(
                                title = title,
                                userNames = usernames,
                                onClick = {
                                    nav.navigate(UserRoutes.Chat(userName = thread.participants!![0].firstName))
                                }
                            )
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

