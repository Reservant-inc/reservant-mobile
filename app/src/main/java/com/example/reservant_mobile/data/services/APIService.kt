package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.data.models.User

interface APIService{
    fun post(user: User)
    fun get()
}

class APIServiceImpl: APIService {
    override fun post(user: User) {
        TODO("Not yet implemented")
    }

    override fun get() {
        TODO("Not yet implemented")
    }

}