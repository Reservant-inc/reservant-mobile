package reservant_mobile.ui.activities

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import reservant_mobile.ui.components.UserCard
import reservant_mobile.ui.viewmodels.TicketViewModel

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.report_title, report.reportId ?: ""),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
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
            TextButton(onClick = { /**TODO**/ }) {
                Text(
                    text = stringResource(R.string.visit_details, visit.visitId ?: 0),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(onClick = { /**TODO**/ }) {
                Text(
                    text = visit.restaurant?.name ?: stringResource(R.string.unknown_restaurant),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ButtonComponent(
            onClick = { /**TODO**/ },
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
            Text(
                text = stringResource(R.string.resolved_by, user.firstName, user.lastName),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
