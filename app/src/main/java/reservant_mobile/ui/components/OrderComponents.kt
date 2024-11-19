package reservant_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.OrderDTO

@Composable
fun OrderItem(order: OrderDTO) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
//        Text(text = stringResource(id = R.string.label_date) + ": ${order.date}")
        Text(text = stringResource(id = R.string.label_total_cost) + ": ${order.cost}")
//        Text(text = order.customer)
        Text(text = order.status!!.toString(), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BadgeFloatingButton(
    icon: ImageVector,
    contentDescription: String?,
    itemCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeBackgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    badgeTextColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    Box(modifier = modifier) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        if (itemCount > 0) {
            Box(
                modifier = Modifier
                    .offset(x = 10.dp, y = (-10).dp)
                    .size(32.dp)
                    .background(badgeBackgroundColor, CircleShape)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = itemCount.toString(),
                    color = badgeTextColor,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}