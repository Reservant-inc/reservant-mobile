package reservant_mobile.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileActivity(navController: NavHostController) {
    val profileViewModel = viewModel<ProfileViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ProfileViewModel() as T
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Tutaj dodaj logikę otwierającą ekran edycji profilu */ }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Profile"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            profileViewModel.user?.let { user ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_logo), //TODO: update to use user photo
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = "${user.firstName} ${user.lastName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Text(text = "5,00 Ocena", color = Color.Gray)
                }

                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Szukaj...") },
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true
                )

                FloatingTabSwitch(pages = listOf(
                    "Chats" to { ChatsTab() },
                    "Friends" to { FriendsTab() },
                    "Events" to { EventsTab(
                        //profileViewModel.getEvents()
                    ) }
                ))
            }
        }
    }
}

@Composable
fun ChatsTab() {
    Text("Chats content goes here")
}

@Composable
fun FriendsTab() {
    Text("Friends content goes here")
}

@Composable
fun EventsTab(
    //events: List<Event>
) {
    LazyColumn {
//        items(events) { event ->
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 8.dp),
//                elevation = 4.dp
//            ) {
//                Row(
//                    modifier = Modifier.padding(16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Image(
//                        painter = painterResource(id = event.imageResource),
//                        contentDescription = "Event Image",
//                        modifier = Modifier.size(50.dp)
//                    )
//                    Column(modifier = Modifier.padding(start = 16.dp)) {
//                        Text(text = event.name, fontWeight = FontWeight.Bold)
//                        Text(text = event.date, color = Color.Gray)
//                        Text(text = event.location, color = Color.Gray)
//                    }
//                }
//
    }
}
