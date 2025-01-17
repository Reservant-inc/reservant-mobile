package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ThreadDTO (
    val threadId: Int? = null,
    val title: String?,
    /***
     * UserDTO contains userId, firstName, lastName, photo
     */
    val participants: List<UserDTO>? = null,
    val type: MessageThreadType? = null
){
    @Serializable
    data class CreateThreadRequest(
        val title: String,
        val participantIds: List<String>
    )
}