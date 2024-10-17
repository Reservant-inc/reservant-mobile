// NewTicketActivity.kt
package reservant_mobile.ui.activities

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.FormInput
import reservant_mobile.ui.components.ComboBox
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.viewmodels.TicketViewModel
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.reservant_mobile.R

@Composable
fun NewTicketActivity() {
    val ticketViewModel = viewModel<TicketViewModel>()
    var formSent by remember { mutableStateOf(false) }
    val categoryExpanded = remember { mutableStateOf(false) }
    val categories = listOf("General Inquiry", "Technical Support", "Billing")

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconWithHeader(
            icon = Icons.Rounded.Add,
            text = stringResource(id = R.string.label_ticket),
        )

        // Topic Field
        FormInput(
            inputText = ticketViewModel.topic,
            onValueChange = { ticketViewModel.topic = it },
            label = stringResource(R.string.label_topic),
            isError = ticketViewModel.isTopicInvalid(),
            errorText = stringResource(R.string.error_topic_required),
            formSent = formSent
        )

        // Category Field
        ComboBox(
            value = ticketViewModel.category,
            onValueChange = { ticketViewModel.category = it },
            label = stringResource(R.string.label_category),
            options = categories,
            isError = ticketViewModel.isCategoryInvalid(),
            errorText = stringResource(R.string.error_category_required),
            expanded = categoryExpanded
        )

        // Message Content Field
        FormInput(
            inputText = ticketViewModel.messageContent,
            onValueChange = { ticketViewModel.messageContent = it },
            label = stringResource(R.string.label_message_content),
            isError = ticketViewModel.isMessageContentInvalid(),
            errorText = stringResource(R.string.error_message_content_required),
            formSent = formSent,
            maxLines = 10,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default)
        )

        // Send Button
        ButtonComponent(
            label = stringResource(R.string.label_send),
            onClick = {
                formSent = true
                if (!ticketViewModel.isTopicInvalid() &&
                    !ticketViewModel.isCategoryInvalid() &&
                    !ticketViewModel.isMessageContentInvalid()
                ) {
                    // Proceed with send action
                }
            }
        )
    }
}
