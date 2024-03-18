package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.data.models.dtos.RegisterUserDTO
import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface IUserService{
    fun registerUser(user: RegisterUserDTO)
    fun loginUser(credentials: LoginCredentialsDTO): Boolean

}


class UserService(private var api: APIService = APIServiceImpl()) : IUserService {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun registerUser(user: RegisterUserDTO) {
//        TODO("post RegisterUserDTO to apiService")
        //TODO("service response from apiService") - later
    }

    override fun loginUser(credentials: LoginCredentialsDTO): Boolean {
        TODO("post LoginCredentialsDTO to apiService")
        //TODO("service response from apiService") - later
    }

}