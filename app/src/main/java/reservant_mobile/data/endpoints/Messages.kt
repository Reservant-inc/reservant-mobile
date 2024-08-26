package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/messages")
class Messages {
    @Resource("{messageId}")
    class MessageId(val parent: Messages = Messages(), val messageId: String) {
        @Resource("mark-read")
        class MarkRead(val parent: MessageId)
    }
}