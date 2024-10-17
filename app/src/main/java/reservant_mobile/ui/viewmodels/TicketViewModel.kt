package reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TicketViewModel : ReservantViewModel() {
    var topic by mutableStateOf("")
    var category by mutableStateOf("")
    var messageContent by mutableStateOf("")

    fun isTopicInvalid(): Boolean {
        return topic.isBlank()
    }

    fun isCategoryInvalid(): Boolean {
        return category.isBlank()
    }

    fun isMessageContentInvalid(): Boolean {
        return messageContent.isBlank()
    }
}
