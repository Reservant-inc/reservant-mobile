package reservant_mobile.ui.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.LoadedPhotoComponent
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.ui.viewmodels.ChatViewModel
import java.time.LocalDate
import java.time.Period


@Composable
fun ChatActivity(navController: NavHostController, threadId: Int, title: String) {
    val chatViewModel: ChatViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ChatViewModel(threadId = threadId) as T
        }
    )

    val messages by rememberUpdatedState(chatViewModel.messagesFlow.collectAsLazyPagingItems())
    val participantsMap by remember {
        mutableStateOf(chatViewModel.participantsMap)
    }

    var currentMessage by remember { mutableStateOf(TextFieldValue()) }

    LaunchedEffect(Unit) {
        chatViewModel.markMessagesAsRead()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        IconWithHeader(
            icon = Icons.AutoMirrored.Rounded.Chat,
            text = title,
            showBackButton = true,
            onReturnClick = { navController.popBackStack() }
        )

        if (chatViewModel.isLoading){
            Box (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ){
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                reverseLayout = true // Display the latest messages at the bottom
            ) {

                if (messages.loadState.refresh is LoadState.Loading){
                    item {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                } else if (messages.loadState.hasError || messages.itemCount < 1) {
                    item {
                        MissingPage(
                            errorString = stringResource(
                                id = R.string.error_threads_not_found
                            )
                        )
                    }
                } else {
                    items(count = messages.itemCount) { index ->
                        val message by remember {
                            mutableStateOf(messages[index])
                        }

                        message?.let { message ->
                            val sender by remember {
                                mutableStateOf(participantsMap[message.authorId])
                            }
                            val isSentByMe by remember {
                                mutableStateOf(message.authorId == chatViewModel.getCurrentUserId())
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = if (isSentByMe) Arrangement.End else Arrangement.Start
                            ) {

                                Column(
                                    horizontalAlignment = if (isSentByMe) Alignment.End else Alignment.Start,

                                ) {

                                   Row(
                                       modifier = Modifier
                                           .padding(bottom = 4.dp)
                                           .widthIn(max = 280.dp),
                                       horizontalArrangement = if (isSentByMe) Arrangement.End else Arrangement.Start
                                   ) {
                                       if (!isSentByMe){
                                           LoadedPhotoComponent(
                                               placeholderModifier = Modifier
                                                   .size(32.dp)
                                                   .align(Alignment.Bottom)
                                                   .clip(CircleShape),
                                               photoModifier = Modifier
                                                   .size(32.dp)
                                                   .align(Alignment.Bottom)
                                                   .clip(CircleShape),
                                               placeholder = R.drawable.ic_profile_placeholder,
                                               getPhoto = {
                                                   sender?.photo?.let{
                                                       chatViewModel.fetchPhoto(it)
                                                   }
                                               }
                                           )
                                       }
                                       Text(
                                           text = message.contents,
                                           modifier = Modifier
                                               .padding(start = 4.dp)
                                               .background(
                                                   if (isSentByMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary,
                                                   shape = RoundedCornerShape(32.dp)
                                               )
                                               .padding(horizontal = 16.dp, vertical = 8.dp),
                                           color = if (isSentByMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                                       )
                                   }

                                    if (!isSentByMe) {
                                        Text(
                                            text = "${stringResource(id = R.string.label_sent_by)}: ${sender?.firstName ?: "Unknown"} ${sender?.lastName ?: "Unknown"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        message.dateSent.ShowDate()
                                    }

                                    if (isSentByMe){
                                        message.dateRead.ShowDate("${stringResource(id = R.string.label_read_at)}: ")

                                        if (message.dateRead == null){
                                            message.dateSent.ShowDate()
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var keyboardOptions: KeyboardOptions by remember {
                mutableStateOf(KeyboardOptions(imeAction = ImeAction.None))
            }

            var enableSend by remember {
                mutableStateOf(false)
            }

            LaunchedEffect(key1 = chatViewModel.isLoading) {
                if (!chatViewModel.isLoading){
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send)
                    enableSend = true
                } else {
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.None)
                    enableSend = false
                }
            }

            OutlinedTextField(
                value = currentMessage,
                onValueChange = { currentMessage = it },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                keyboardOptions = keyboardOptions,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (currentMessage.text.isNotBlank()) {
                                chatViewModel.createMessage(currentMessage.text)
                                currentMessage = TextFieldValue()
                            }
                        },
                        enabled = enableSend
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                },
                singleLine = true
            )

        }
    }
}


@Composable
fun String?.ShowDate(prefix: String = ""){
    this?.let {
        val parsedDate by remember {
            mutableStateOf(formatToDateTime(it))
        }

        val formattedDate by remember {
            mutableStateOf(formatToDateTime(it, "dd.MM.yyyy"))
        }
        val formattedTime by remember {
            mutableStateOf(formatToDateTime(it, "HH:mm"))
        }

        val isDateToday by remember {
            mutableStateOf(
                Period.between(LocalDate.now(), parsedDate.toLocalDate()).days == 0
            )
        }

        if (isDateToday) {
            Text(
                text = "$prefix$formattedTime",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Text(
                text = "$prefix$formattedDate $formattedTime",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }


}