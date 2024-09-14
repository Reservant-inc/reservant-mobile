package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/notifications")
class Notifications(
    val unreadOnly:Boolean? = null,
    val page: Int? = null,
    val perPage: Int? = null,
) {
    @Resource("bubbles")
    class Bubbles(val parent: Notifications = Notifications())

    @Resource("mark-read")
    class MarkRead(val parent: Notifications = Notifications())
}