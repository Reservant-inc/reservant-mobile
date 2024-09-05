package reservant_mobile.ui.viewmodels

import androidx.lifecycle.ViewModel
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
import reservant_mobile.data.models.dtos.ThreadDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.IThreadsService
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.ThreadsService
import reservant_mobile.data.services.UserService

class ChatViewModel(
    private val threadsService: IThreadsService = ThreadsService(),
    private val userService: IUserService = UserService()
) : ViewModel() {

    // TODO: Replace with dynamic thread ID in the future
    private val threadId: Any = 1

    // StateFlow to hold the paging data
    private val _messagesFlow = MutableStateFlow<Flow<PagingData<MessageDTO>>?>(null)
    val messagesFlow: StateFlow<Flow<PagingData<MessageDTO>>?> = _messagesFlow

    // State to hold participant information
    private val _participantsMap = mutableMapOf<String, UserDTO>()
    val participantsMap: Map<String, UserDTO> get() = _participantsMap

    init {
        fetchThread()
    }

    fun getCurrentUserId(): String? {
        return UserService.UserObject.userId
    }

    private fun fetchThread() {
        viewModelScope.launch {
            val result: Result<ThreadDTO?> = threadsService.getThread(threadId)

            if (!result.isError) {
                result.value?.participants?.forEach { participant ->
                    participant.userId?.let { userId ->
                        _participantsMap[userId] = participant
                    }
                }
                fetchMessages()
            } else {
                val errors = result.errors
                // You can handle errors here, such as displaying a toast or logging
            }
        }
    }


    private fun fetchMessages() {
        viewModelScope.launch {
            val result: Result<Flow<PagingData<MessageDTO>>?> = threadsService.getMessages(threadId)

            if (!result.isError) {
                _messagesFlow.value = result.value?.cachedIn(viewModelScope)
            } else {
                val errors = result.errors
                // You can handle errors here, such as displaying a toast or logging
            }
        }
    }

    fun createMessage(contents: String) {
        viewModelScope.launch {
            val result: Result<MessageDTO?> = threadsService.createMessage(threadId, contents)

            if (!result.isError) {
                fetchMessages()  // Refresh messages
            } else {
                val errors = result.errors
                // You can handle errors here, such as displaying a toast or logging
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
                            val errors = result.errors
                            // You can handle errors here
                        }
                    }!!
                }
            }
        }
    }
}

