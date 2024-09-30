package reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/auth")
class Auth {
    @Resource("register-restaurant-employee")
    class RegisterRestaurantEmployee(val parent: Auth = Auth())
    @Resource("register-customer-support-agent")
    class RegisterCustomerSupportAgent(val parent: Auth = Auth())
    @Resource("login")
    class Login(val parent: Auth = Auth())
    @Resource("register-customer")
    class RegisterCustomer(val parent: Auth = Auth())
    @Resource("is-unique-login")
    class IsUniqueLogin(val parent: Auth = Auth(), val login: String? = "")
    @Resource("refresh-token")
    class RefreshToken(val parent: Auth = Auth())
    @Resource("register-firebase-token")
    class RegisterFirebaseToken(val parent: Auth = Auth())
    @Resource("unregister-firebase-token")
    class UnregisterFirebaseToken(val parent: Auth = Auth())

}