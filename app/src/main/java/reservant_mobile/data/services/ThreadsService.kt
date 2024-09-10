package reservant_mobile.data.services

import androidx.paging.PagingData
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import reservant_mobile.data.endpoints.Messages
import reservant_mobile.data.endpoints.Threads
import reservant_mobile.data.models.dtos.MessageDTO
import reservant_mobile.data.models.dtos.PageDTO
import reservant_mobile.data.models.dtos.ThreadDTO
import reservant_mobile.data.models.dtos.fields.Result

interface IThreadsService{
    suspend fun createThread(title: String, participantIds: List<String>): Result<ThreadDTO?>
    suspend fun editThread(threadId: Any, thread: ThreadDTO): Result<ThreadDTO?>
    suspend fun deleteThread(threadId: Any): Result<Boolean>
    suspend fun getThread(threadId: Any): Result<ThreadDTO?>
    suspend fun createMessage(threadId: Any, contents: String): Result<MessageDTO?>
    suspend fun getMessages(threadId: Any): Result<Flow<PagingData<MessageDTO>>?>
    suspend fun editMessage(messageId: Any, contents: String): Result<MessageDTO?>
    suspend fun deleteMessage(messageId: Any): Result<Boolean>
    suspend fun markMessageAsRead(messageId: Any): Result<MessageDTO?>
    suspend fun addParticipant(threadId: Any, userId: String): Result<Boolean>
    suspend fun removeParticipant(threadId: Any, userId: String): Result<Boolean>

}

@OptIn(InternalSerializationApi::class)
class ThreadsService: ServiceUtil(), IThreadsService {
    override suspend fun createThread(title: String, participantIds: List<String>): Result<ThreadDTO?> {
        val thread = mapOf(
            "title" to title,
            "participantIds" to participantIds
        )
        val res = api.post(Threads(), thread)
        return complexResultWrapper(res)
    }

    override suspend fun editThread(threadId: Any, thread: ThreadDTO): Result<ThreadDTO?> {
        val res = api.put(Threads.ThreadId(threadId = threadId.toString()), thread)
        return complexResultWrapper(res)
    }

    override suspend fun deleteThread(threadId: Any): Result<Boolean> {
        val res = api.delete(Threads.ThreadId(threadId = threadId.toString()))
        return booleanResultWrapper(res)
    }

    override suspend fun getThread(threadId: Any): Result<ThreadDTO?> {
        val res = api.get(Threads.ThreadId(threadId = threadId.toString()))
        return complexResultWrapper(res)
    }

    override suspend fun createMessage(threadId: Any, contents: String): Result<MessageDTO?> {
        val message = mapOf(
            "contents" to contents
        )
        val res = api.post(Threads.ThreadId.Messages(Threads.ThreadId(threadId = threadId.toString())), message)
        return complexResultWrapper(res)
    }

    override suspend fun getMessages(threadId: Any): Result<Flow<PagingData<MessageDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            Threads.ThreadId.Messages(
                parent = Threads.ThreadId(threadId = threadId.toString()),
                page = page,
                perPage = perPage
            )
        )}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(MessageDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun editMessage(messageId: Any, contents: String): Result<MessageDTO?> {
        val message = mapOf(
            "contents" to contents
        )
        val res = api.put(Messages.MessageId(messageId = messageId.toString()), message)
        return complexResultWrapper(res)
    }

    override suspend fun deleteMessage(messageId: Any): Result<Boolean> {
        val res = api.delete(Messages.MessageId(messageId = messageId.toString()))
        return booleanResultWrapper(res)
    }

    override suspend fun markMessageAsRead(messageId: Any): Result<MessageDTO?> {
        val res = api.post(Messages.MessageId.MarkRead(
            parent = Messages.MessageId(messageId = messageId.toString())),
            obj = ""
        )
        return complexResultWrapper(res)
    }

    override suspend fun addParticipant(threadId: Any, userId: String): Result<Boolean> {
        val message = mapOf(
            "userId" to userId
        )
        val res = api.post(Threads.ThreadId.AddParticipant(Threads.ThreadId(threadId = threadId.toString())), message)
        return booleanResultWrapper(res)
    }

    override suspend fun removeParticipant(threadId: Any, userId: String): Result<Boolean> {
        val message = mapOf(
            "userId" to userId
        )
        val res = api.post(Threads.ThreadId.RemoveParticipant(Threads.ThreadId(threadId = threadId.toString())), message)
        return booleanResultWrapper(res)
    }

}