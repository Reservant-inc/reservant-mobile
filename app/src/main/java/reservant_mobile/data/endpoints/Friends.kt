package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/friends")
class Friends(val page: Int? = null, val perPage: Int? = null) {
    @Resource("{userId}")
    class UserId(val parent: Friends = Friends(), val userId: String) {
        @Resource("send-request")
        class SendRequest(val parent: UserId)
    }

    @Resource("{senderId}")
    class SenderId(val parent: Friends = Friends(), val senderId: String) {
        @Resource("mark-read")
        class MarkRead(val parent: SenderId)

        @Resource("accept-request")
        class AcceptRequest(val parent: SenderId)
    }

    @Resource("incoming")
    class Incoming(val parent: Friends = Friends(), val page: Int? = null, val perPage: Int? = null)

    @Resource("outgoing")
    class Outgoing(val parent:  Friends = Friends(), val page: Int? = null, val perPage: Int? = null)
}