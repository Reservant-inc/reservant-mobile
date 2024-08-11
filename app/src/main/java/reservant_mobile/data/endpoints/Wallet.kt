package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/wallet")
class Wallet{

    @Resource("add-money")
    class AddMoney(val parent: Wallet = Wallet())

    @Resource("status")
    class Status(val parent: Wallet = Wallet())

    @Resource("history")
    class History(val parent: Wallet = Wallet(), val page: Int? = null, val perPage: Int? = null)

}
