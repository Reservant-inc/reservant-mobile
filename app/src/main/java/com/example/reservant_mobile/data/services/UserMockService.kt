package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.data.models.User
import com.example.reservant_mobile.data.models.dtos.LoginDTO

interface IUserService{
    fun postUser(user: User)

}


class UserMockService(
    private var api: APIService = APIServiceImpl()
) : IUserService {
    override fun postUser(user: User) {
        api.post(
            LoginDTO("", "")
        )
    }

}