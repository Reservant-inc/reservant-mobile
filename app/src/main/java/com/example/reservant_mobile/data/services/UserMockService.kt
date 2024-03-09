package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.data.models.dtos.RegisterUserDTO
import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO

interface IUserService{
    fun registerUser(user: RegisterUserDTO)
    fun loginUser(credentials: LoginCredentialsDTO): Boolean

}


class UserMockService(
    private var api: APIService = APIServiceImpl()
) : IUserService {
    override fun registerUser(user: RegisterUserDTO) {
        TODO("post RegisterUserDTO to apiService")
        //TODO("service response from apiService") - later
    }

    override fun loginUser(credentials: LoginCredentialsDTO): Boolean {
        TODO("post LoginCredentialsDTO to apiService")
        //TODO("service response from apiService") - later
    }

}