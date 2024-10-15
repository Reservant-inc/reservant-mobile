package reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.rounded.PersonPin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.MyFloatingActionButton
import reservant_mobile.ui.components.SearchBarWithFilter
import reservant_mobile.ui.components.UserCard
import reservant_mobile.ui.navigation.MainRoutes
import reservant_mobile.ui.navigation.UserRoutes
import reservant_mobile.ui.viewmodels.SocialViewModel

@Composable
fun SocialActivity(navController: NavHostController){
    val viewmodel = viewModel<SocialViewModel>()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 8.dp)
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

                SearchBarWithFilter(
                    searchQuery = query,
                    onSearchQueryChange = { query = it },
                    onFilterSelected = {},
                    currentFilter = "",
                    filterOptions = listOf()
                )

                Spacer(modifier = Modifier.size(32.dp))
            }

            items(7) {
                UserCard()
            }
        }


    }
}