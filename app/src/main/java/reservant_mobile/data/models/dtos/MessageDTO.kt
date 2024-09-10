package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable


@Serializable
data class MessageDTO (
    val messageId: Int? = null,
    val contents: String,
    /***
     * Date in 'yyyy-MM-dd'T'HH:mm:ss.yyyy'Z'' format
     */
    val dateSent: String? = null,
    /***
     * Date in 'yyyy-MM-dd'T'HH:mm:ss.yyyy'Z'' format
     */
    val dateRead: String? = null,
    val authorId: String? = null,
    val messageThreadId: Int? = null
)