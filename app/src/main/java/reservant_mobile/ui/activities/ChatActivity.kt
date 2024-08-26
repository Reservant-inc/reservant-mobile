package reservant_mobile.ui.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import reservant_mobile.data.models.dtos.MessageDTO
import reservant_mobile.ui.navigation.UserRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatActivity(navController: NavHostController, userName: String) {
    val messages = remember {
        mutableStateListOf(
            MessageDTO(
                messageId = 1,
                contents = "Hello!",
                authorsFirstName = "John",
                authorsLastName = "Doe",
                dateSent = "2024-08-24T10:15:30.000Z"
            ),
            MessageDTO(
                messageId = 2,
                contents = "Hi, how are you?",
                authorsFirstName = "Jane",
                authorsLastName = "Smith",
                dateSent = "2024-08-24T10:16:00.000Z"
            ),
            MessageDTO(
                messageId = 3,
                contents = "I'm good, thanks! How about you?",
                authorsFirstName = "John",
                authorsLastName = "Doe",
                dateSent = "2024-08-24T10:17:30.000Z"
            ),
            MessageDTO(
                messageId = 4,
                contents = "Doing well, thank you.",
                authorsFirstName = "Jane",
                authorsLastName = "Smith",
                dateSent = "2024-08-24T10:18:00.000Z"
            )
        )
    }
    var currentMessage by remember { mutableStateOf(TextFieldValue()) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                            ) {
                                // Placeholder for user icon
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = userName)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(UserRoutes.ChatList) }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                messages.forEach { message ->
                    val isSentByMe = message.authorsFirstName == "John" && message.authorsLastName == "Doe"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (isSentByMe) Arrangement.End else Arrangement.Start
                    ) {
                        Column(
                            horizontalAlignment = if (isSentByMe) Alignment.End else Alignment.Start,
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Text(
                                text = message.contents,
                                modifier = Modifier
                                    .background(
                                        if (isSentByMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                        shape = CircleShape
                                    )
                                    .padding(8.dp),
                                color = if (isSentByMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sent by: ${message.authorsFirstName} ${message.authorsLastName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            message.dateSent?.let { date ->
                                Text(
                                    text = date,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = currentMessage,
                    onValueChange = { currentMessage = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .background(Color.LightGray, shape = CircleShape)
                        .padding(16.dp)
                )
                IconButton(onClick = {
                    if (currentMessage.text.isNotBlank()) {
                        messages.add(
                            MessageDTO(
                                messageId = messages.size + 1,
                                contents = currentMessage.text,
                                authorsFirstName = "John",
                                authorsLastName = "Doe",
                                dateSent = "2024-08-24T10:20:00.000Z"
                            )
                        )
                        currentMessage = TextFieldValue()
                    }
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}
