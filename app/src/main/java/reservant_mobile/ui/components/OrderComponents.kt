package reservant_mobile.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        Text(text = order.status!!, fontWeight = FontWeight.Bold)
    }
}