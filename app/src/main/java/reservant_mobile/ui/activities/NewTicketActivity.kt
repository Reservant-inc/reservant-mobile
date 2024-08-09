package reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reservant_mobile.R
import reservant_mobile.ui.components.IconWithHeader

@Composable
fun NewTicketActivity() {
    var topic by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var messageContent by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconWithHeader(
            icon = Icons.Rounded.Add,
            text = stringResource(id = R.string.label_ticket),
        )
        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("Topic") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            trailingIcon = {
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Category")
            },
            readOnly = true,
            // Add category selection logic here
        )
        OutlinedTextField(
            value = messageContent,
            onValueChange = { messageContent = it },
            label = { Text("Message content...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(200.dp),
            maxLines = 10
        )
        Button(
            onClick = { /* TODO: Handle send action */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "WYŚLIJ", color = Color.White, fontSize = 18.sp)
        }
    }
}
