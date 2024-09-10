package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/threads")
class Threads {
    @Resource("{threadId}")
    class ThreadId(val parent: Threads = Threads(), val threadId: String) {
        @Resource("messages")
        class Messages(
            val parent: ThreadId,
            val page: Int? = null,
            val perPage: Int? = null
        )

        @Resource("add-participant")
        class AddParticipant(val parent: ThreadId)

        @Resource("remove-participant")
        class RemoveParticipant(val parent: ThreadId)
    }
}