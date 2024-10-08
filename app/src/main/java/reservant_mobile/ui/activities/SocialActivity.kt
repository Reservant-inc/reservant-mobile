package reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.MyFloatingActionButton
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.navigation.RegisterRestaurantRoutes
import reservant_mobile.ui.navigation.UserRoutes
import reservant_mobile.ui.viewmodels.SocialViewModel

@Composable
fun SocialActivity(){
    val viewmodel = viewModel<SocialViewModel>()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = MainRoutes.Social){
        composable<MainRoutes.Social> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp, horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    IconWithHeader(
                        icon = Icons.Rounded.PersonSearch,
                        text = stringResource(R.string.label_social)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    MyFloatingActionButton(
                        onClick = {
                            navController.navigate(UserRoutes.ChatList)
                        },
                        icon = Icons.AutoMirrored.Filled.Chat
                    )
                }
            }
        }
        composable<UserRoutes.ChatList> {
            ChatListActivity(navController = navController)
        }


    }


}