package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.data.models.User

interface IUserService{
    fun postUser(user: User)

}


class UserService(
    private var api: APIService = APIServiceImpl()
) : IUserService {
    override fun postUser(user: User) {
        api.post(user)
    }

}