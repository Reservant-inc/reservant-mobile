package reservant_mobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.UserService

class ProfileViewModel(
    private val userService: IUserService = UserService()
) : ViewModel() {

    init {

        user = userService.getUser()



    }


}