package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.data.models.dtos.RegisterUserDTO
import com.example.reservant_mobile.data.models.dtos.LoginCredentialsDTO


interface IUserService{
    suspend fun registerUser(user: RegisterUserDTO): Any?
    suspend fun loginUser(credentials: LoginCredentialsDTO): Any?
}

class UserService(private var api: APIService = APIServiceImpl()) : IUserService {

    override suspend fun registerUser(user: RegisterUserDTO): Any? {
//        TODO("point endpoint to /res/.../endpoints.xml")
        val res = api.post(user,"/auth/register-customer") ?: return null
        return res.status.value
    }

    override suspend fun loginUser(credentials: LoginCredentialsDTO): Any? {
//        TODO("point endpoint to /res/.../endpoints.xml")
        val res = api.post(credentials,"/auth/login") ?: return null
        return res.status.value
    }

}