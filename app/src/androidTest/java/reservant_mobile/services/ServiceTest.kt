package reservant_mobile.services

import reservant_mobile.data.models.dtos.LoginCredentialsDTO
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.LocalBearerService
import reservant_mobile.data.services.UserService
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before

abstract class ServiceTest {
    private lateinit var existingToken: String
    protected val localBearer = LocalBearerService()
    protected val userService: IUserService = UserService()
    protected var loginUser: LoginCredentialsDTO = LoginCredentialsDTO(
                                                    login = "JD",
                                                    password = "Pa${"$"}${"$"}w0rd",
                                                    rememberMe = false
                                                    )
    @Before
    fun getPreTestData() = runBlocking {
        existingToken = localBearer.getBearerToken()
    }

    @After
    fun cleanPostTestData() = runBlocking{
        userService.logoutUser()
        if(existingToken.isNotEmpty())
            localBearer.saveBearerToken(existingToken)
    }

    suspend fun loginUser(){
        userService.loginUser(loginUser)
    }


}