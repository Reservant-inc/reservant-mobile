package com.example.reservant_mobile.ui.activities

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.reservant_mobile.ui.components.InputUserFile
import com.example.reservant_mobile.ui.components.TagsSelection
import com.example.reservant_mobile.ui.components.UserButton

@Composable
fun RegisterRestaurantActivity_3(navController: NavHostController) {

    val tags = listOf("na miejscu", "na wynos", "azjatyckie", "włoskie", "tag1", "tag2")
    var selectedTags = remember { mutableStateListOf<String>() }
    var delivery by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Text(text = "Rejestracja lokalu krok 3", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Wybierz tagi, które opisują twój lokal", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        TagsSelection(
            tags = tags,
            selectedTags = selectedTags,
            onTagSelected = { tag, isSelected ->
                if (isSelected) {
                    selectedTags.add(tag)
                } else {
                    selectedTags.remove(tag)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dostawa na naszym pośrednictwem:",
                modifier = Modifier.weight(1f)
            )

            Row {
                RadioButton(
                    selected = delivery,
                    onClick = { delivery = true }
                )
                Text(
                    text = "tak",
                    modifier = Modifier
                        .clickable { delivery = true }
                        .padding(end = 8.dp)
                        .padding(top = 16.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = !delivery,
                    onClick = { delivery = false }
                )
                Text(
                    text = "nie",
                    modifier = Modifier
                        .clickable { delivery = false }
                        .padding(start = 8.dp)
                        .padding(top = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // File upload and description
        Column {
            InputUserFile(
                label = "Logo, zdjęcia lokalu",
                onFilePicked = {
                    // ...
                }
            )
            TextField(
                value = "Opis lokalu",
                onValueChange = { /* Handle description input */ },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        UserButton(
            label = "Zapisz",
            onClick = { /* Handle file add */ })
    }


}