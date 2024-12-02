package reservant_mobile.services

import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import reservant_mobile.data.constants.PrefsKeys
import reservant_mobile.data.models.dtos.LoginCredentialsDTO
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.LocalDataService
import reservant_mobile.data.services.UserService

abstract class ServiceTest {
    private lateinit var existingToken: String
    protected val localBearer = LocalDataService()
    protected val userService: IUserService = UserService()
    protected var loginUser: LoginCredentialsDTO = LoginCredentialsDTO(
                                                    login = "JD",
                                                    password = "Pa${"$"}${"$"}w0rd",
                                                    rememberMe = false
                                                    )
    @Before
    fun getPreTestData() = runBlocking {
        existingToken = localBearer.getData(PrefsKeys.BEARER_TOKEN)
    }

    @After
    fun cleanPostTestData() = runBlocking{
        userService.logoutUser()
        if(existingToken.isNotEmpty())
            localBearer.saveData(PrefsKeys.BEARER_TOKEN,existingToken)
    }

    suspend fun loginUser(user: LoginCredentialsDTO = loginUser){
        userService.loginUser(user)
    }


}