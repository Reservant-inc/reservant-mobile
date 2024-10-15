package reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PersonPin
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.UserCard
import reservant_mobile.ui.viewmodels.SocialViewModel
import androidx.compose.material3.IconButton
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@Composable
fun SocialActivity(navController: NavHostController){
    val viewmodel = viewModel<SocialViewModel>()
    val users by rememberUpdatedState(viewmodel.users.collectAsLazyPagingItems())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            item {
                IconWithHeader(
                    icon = Icons.Rounded.PersonPin,
                    text = stringResource(R.string.label_social),
                    showBackButton = true,
                    onReturnClick = { navController.popBackStack() }
                )

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
                                viewmodel.viewModelScope.launch {
                                    viewmodel.getUsers(query)
                                }
                            }
                        ){
                            Icon(imageVector = Icons.Rounded.Search, contentDescription = "Send")
                        }
                    },
                    singleLine = true
                )

            }

            items(3){
                UserCard(firstName = "John", lastName = "Doe") {
                    null
                }
            }


            items(users.itemCount) { i ->
                val user = users[i]

                user?.let {
                    UserCard(
                        firstName = it.firstName,
                        lastName = it.lastName,
                        getImage = {
                            user.photo?.let { photo ->
                                viewmodel.getPhoto(photo)
                            }
                        }
                    )

                }

            }
        }


    }
}