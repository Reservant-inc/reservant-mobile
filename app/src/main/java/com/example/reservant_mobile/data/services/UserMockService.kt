package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.data.models.User
import kotlinx.serialization.json.Json

interface IUserService{
    fun postUser(user: User)

}


class UserMockService(
    private var api: APIService = APIServiceImpl()
) : IUserService {
    override fun postUser(user: User) {
        api.post(
            user
        )
    }

}