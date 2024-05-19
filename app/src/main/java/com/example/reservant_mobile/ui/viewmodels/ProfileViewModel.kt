package com.example.reservant_mobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.reservant_mobile.data.services.UserService

class ProfileViewModel(private val userService: UserService = UserService()) : ViewModel() {
    suspend fun logout() {
        userService.logoutUser()
    }

}