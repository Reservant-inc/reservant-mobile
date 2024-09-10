package reservant_mobile.ui.activities

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.MessageDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.IThreadsService
import reservant_mobile.data.services.ThreadsService
import reservant_mobile.ui.viewmodels.ReservantViewModel

class ChatViewModel(
    private val threadsService: IThreadsService = ThreadsService()
) : ReservantViewModel() {

    // TODO: Replace with dynamic thread ID in the future
    private val threadId: Any = 1

    // StateFlow to hold the paging data
    private val _messagesFlow = MutableStateFlow<Flow<PagingData<MessageDTO>>?>(null)
    val messagesFlow: StateFlow<Flow<PagingData<MessageDTO>>?> = _messagesFlow

    init {
        fetchMessages()
    }

    private fun fetchMessages() {
        viewModelScope.launch {
            val result: Result<Flow<PagingData<MessageDTO>>?> = threadsService.getMessages(threadId)

            if (!result.isError) {
                _messagesFlow.value = result.value?.cachedIn(viewModelScope)
            } else {
                // Handle error scenario
                val errors = result.errors
                // Możesz tutaj obsłużyć błędy, np. wyświetlić toast lub zapisać log
            }
        }
    }

    fun createMessage(contents: String) {
        viewModelScope.launch {
            val result: Result<MessageDTO?> = threadsService.createMessage(threadId, contents)

            if (!result.isError) {
                // Message successfully created, you might want to refresh messages or append to the list
                fetchMessages()  // Refresh messages
            } else {
                // Handle error scenario
                val errors = result.errors
                // Możesz tutaj obsłużyć błędy, np. wyświetlić toast lub zapisać log
            }
        }
    }

    fun markMessagesAsRead() {
        viewModelScope.launch {
            _messagesFlow.value?.collectLatest { pagingData ->
                pagingData.map { message ->
                    message.messageId?.let { messageId ->
                        val result: Result<MessageDTO?> = threadsService.markMessageAsRead(messageId)
                        if (result.isError) {
                            // Obsługa błędu, jeśli wystąpi
                            val errors = result.errors
                            // Możesz tutaj obsłużyć błędy
                        }
                    }!!
                }
            }
        }
    }
}
