package reservant_mobile.data.services

import androidx.paging.PagingData
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import reservant_mobile.data.endpoints.Friends
import reservant_mobile.data.models.dtos.FriendRequestDTO
import reservant_mobile.data.models.dtos.PageDTO
import reservant_mobile.data.models.dtos.fields.Result

interface IFriendsService{
    suspend fun sendFriendRequest(userId: Any): Result<Boolean>
    suspend fun markRequestAsRead(senderId: Any): Result<Boolean>
    suspend fun acceptFriendRequest(senderId: Any): Result<Boolean>
    suspend fun deleteFriendOrRequest(userId: Any): Result<Boolean>
    suspend fun getFriends(): Result<Flow<PagingData<FriendRequestDTO>>?>
    suspend fun getIncomingFriendRequests(): Result<Flow<PagingData<FriendRequestDTO>>?>
    suspend fun getOutgoingFriendRequests(): Result<Flow<PagingData<FriendRequestDTO>>?>
}

@OptIn(InternalSerializationApi::class)
class FriendsService:ServiceUtil(), IFriendsService {
    override suspend fun sendFriendRequest(userId: Any): Result<Boolean> {
        val res = api.post(Friends.UserId.SendRequest(Friends.UserId(userId = userId.toString())), "")
        return booleanResultWrapper(res)
    }

    override suspend fun markRequestAsRead(senderId: Any): Result<Boolean> {
        val res = api.post(Friends.SenderId.MarkRead(Friends.SenderId(senderId = senderId.toString())), "")
        return booleanResultWrapper(res)
    }

    override suspend fun acceptFriendRequest(senderId: Any): Result<Boolean> {
        val res = api.post(Friends.SenderId.AcceptRequest(Friends.SenderId(senderId = senderId.toString())), "")
        return booleanResultWrapper(res)
    }

    override suspend fun deleteFriendOrRequest(userId: Any): Result<Boolean> {
        val res = api.delete(Friends.UserId(userId = userId.toString()))
        return booleanResultWrapper(res)
    }

    override suspend fun getFriends(): Result<Flow<PagingData<FriendRequestDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            Friends(
                page = page,
                perPage = perPage
            ))}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(FriendRequestDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun getIncomingFriendRequests(): Result<Flow<PagingData<FriendRequestDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            Friends.Incoming(
                page = page,
                perPage = perPage
            ))}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(FriendRequestDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }

    override suspend fun getOutgoingFriendRequests(): Result<Flow<PagingData<FriendRequestDTO>>?> {
        val call : suspend (Int, Int) -> Result<HttpResponse?> = { page, perPage -> api.get(
            Friends.Outgoing(
                page = page,
                perPage = perPage
            ))}

        val sps = ServicePagingSource(call, serializer = PageDTO.serializer(FriendRequestDTO::class.serializer()))
        return pagingResultWrapper(sps)
    }


}