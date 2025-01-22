package reservant_mobile.ui.activities

import TicketViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.rounded.Details
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.ReportDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.UserCard
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.navigation.UserRoutes

@Composable
fun ReportDetailsActivity(
    report: ReportDTO,
    navController: NavController
) {
    val reportViewModel: TicketViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TicketViewModel() as T
            }
        }
    )
    val title = stringResource(R.string.report_title, report.reportId ?: "")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconWithHeader(
            icon = Icons.Rounded.Details,
            text = title,
            showBackButton = true,
            onReturnClick = {
                navController.popBackStack()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.category, report.category ?: ""),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val formattedDate =
            report.reportDate?.let { formatToDateTime(it, "dd MMMM yyyy | HH:mm") } ?: ""
        Text(
            text = stringResource(R.string.date, formattedDate),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.description, report.description ?: ""),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        report.createdBy?.let { user ->
            UserCard(
                firstName = user.firstName, lastName = user.lastName, getImage = {
                    user.photo?.let { photo ->
                        reportViewModel.getPhoto(photo)
                    }
                }
            )
        }

        report.reportedUser?.let { user ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.reported_user_report),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            UserCard(
                firstName = user.firstName, lastName = user.lastName, getImage = {
                    user.photo?.let { photo ->
                        reportViewModel.getPhoto(photo)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        report.visit?.let { visit ->
            ButtonComponent(
                onClick = { navController.navigate(UserRoutes.VisitDetails(visit.visitId ?: 0)) },
                label = stringResource(R.string.visit_details, visit.visitId ?: 0),
                icon = Icons.Default.Visibility
            )

            Spacer(modifier = Modifier.height(8.dp))

            ButtonComponent(
                onClick = { navController.navigate(RestaurantRoutes.Details(restaurantId = report.visit.restaurant?.restaurantId ?: 0)) },
                label = visit.restaurant?.name ?: stringResource(R.string.unknown_restaurant),
                icon = Icons.Default.Restaurant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ButtonComponent(
            onClick = { navController.navigate(UserRoutes.Chat(threadId = report.threadId ?: 0, threadTitle = title)) },
            label = stringResource(R.string.open_chat),
            icon = Icons.Default.Chat
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.status, report.reportStatus ?: ""),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        report.resolutionComment?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.resolution_comment, it),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        report.resolvedBy?.let { user ->
            Spacer(modifier = Modifier.height(8.dp))
            UserCard(
                firstName = user.firstName, lastName = user.lastName, getImage = {
                    user.photo?.let { photo ->
                        reportViewModel.getPhoto(photo)
                    }
                }
            )
        }

        report.resolutionDate?.let {
            Spacer(modifier = Modifier.height(8.dp))
            val formattedResolutionDate = formatToDateTime(it, "dd MMMM yyyy | HH:mm")
            Text(
                text = stringResource(R.string.resolution_date, formattedResolutionDate),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
