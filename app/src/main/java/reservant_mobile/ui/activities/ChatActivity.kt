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
            MessageDTO("Hello!", true),
            MessageDTO("Hi, how are you?", false),
            MessageDTO("I'm good, thanks! How about you?", true),
            MessageDTO("Doing well, thank you.", false)
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (message.isSentByMe) Arrangement.End else Arrangement.Start
                    ) {
                        Text(
                            text = message.text,
                            modifier = Modifier
                                .background(
                                    if (message.isSentByMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                    shape = CircleShape
                                )
                                .padding(8.dp),
                            color = if (message.isSentByMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                        )
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
                        messages.add(MessageDTO(currentMessage.text, true))
                        currentMessage = TextFieldValue()
                    }
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}
