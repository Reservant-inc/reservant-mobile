package reservant_mobile.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate


// TODO: resources
@Composable
fun ReservationFloatingMenu(
    onDineInClick: () -> Unit,
    onDeliveryClick: () -> Unit,
    onTakeawayClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(onClick = { expanded = false })
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .height(680.dp)
                        .width(360.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    FloatingTabSwitch(
                        pages = listOf(
                            "Na miejscu" to {
                                DineInContent(
                                    onDineInClick,
                                    modifier = Modifier.padding(top = 88.dp)
                                )
                            },
                            "Dostawa" to { // TODO: not implemented on backend
                                DeliveryContent(
                                    onDeliveryClick,
                                    modifier = Modifier.padding(top = 88.dp)
                                )
                            },
                            "Odbiór" to { // TODO: not implemented on backend
                                TakeawayContent(
                                    onTakeawayClick,
                                    modifier = Modifier.padding(top = 88.dp)
                                )
                            }
                        ),
                        paneScroll = false
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(onClick = { expanded = !expanded }) {
                Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Plecak")
            }
        }
    }
}

@Composable
fun DeliveryContent(
    onDeliveryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = "Dostawa", style = MaterialTheme.typography.headlineSmall)

        Button(onClick = onDeliveryClick) {
            Text("Zamów dostawę")
        }
    }
}

@Composable
fun TakeawayContent(
    onTakeawayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp, end = 8.dp, start = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mój koszyk",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Danie1", style = MaterialTheme.typography.bodyLarge)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "ilość: 1", style = MaterialTheme.typography.bodyLarge)
                                IconButton(
                                    onClick = { /* TODO: Decrease item count */ },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Remove"
                                    )
                                }
                                IconButton(
                                    onClick = { /* TODO: Increase item count */ },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add"
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Kwota: 30zł",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = "",
            onValueChange = { /* TODO: Handle note change */ },
            label = { Text(text = "Napisz notatkę do zamówienia...") },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = "JSKS6X293",
            onValueChange = { /* TODO: Change promo code */ },
            label = {
                Text(
                    text = "Wpisz kod promocyjny",
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Kwota całkowita:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "60zł",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { /* TODO: Go to summary */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Text(text = "Przejdź do podsumowania")
            }
        }
    }
}

@Composable
fun DineInContent(
    onDineInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var comment by remember { mutableStateOf("") }
    var seats by remember { mutableIntStateOf(1) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Moja rezerwacja",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
        )
        MyDatePickerDialog(
            label = { Text("Data rezerwacji") },
            onDateChange = { selectedDate ->
                // TODO: date change
            },
            startDate = LocalDate.now().toString(),
            allowFutureDates = true
        )

        Text(
            text = "Liczba miejsc",
            style = MaterialTheme.typography.bodyLarge
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { if (seats > 1) seats-- },
                color = MaterialTheme.colorScheme.primary,
                enabled = seats > 1,
                icon = "-"
            )
            Text(
                text = seats.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = { if (seats < 10) seats++ },
                color = MaterialTheme.colorScheme.primary,
                enabled = seats < 10,
                icon = "+"
            )
        }
        OutlinedTextField(
            value = "",
            onValueChange = { /* TODO: Handle note change */ },
            label = { Text(text = "Napisz notatkę do zamówienia...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(8.dp)
        )

        Text(
            text = "Mój koszyk",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                .padding(start = 16.dp, end = 16.dp)
                .padding(vertical = 8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Danie1", style = MaterialTheme.typography.bodyLarge)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "ilość: 1", style = MaterialTheme.typography.bodyLarge)
                        IconButton(
                            onClick = { /* TODO: Decrease item count */ },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Remove")
                        }
                        IconButton(
                            onClick = { /* TODO: Increase item count */ },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                }
                Text(
                    text = "Kwota: 30zł",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        OutlinedTextField(
            value = "JSKS6X293",
            onValueChange = { /* TODO: Change promo code */ },
            label = {
                Text(
                    text = "Wpisz kod promocyjny",
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)

        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Kwota całkowita:",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "60zł",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = { /* TODO: Go to summary */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text(text = "Przejdź do podsumowania")
        }
    }
}