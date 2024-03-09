package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.data.models.User
import com.example.reservant_mobile.data.models.dtos.LoginDTO

interface APIService{
    fun post(loginDTO: LoginDTO)
    fun get()
}

class APIServiceImpl: APIService {
    override fun post(loginDTO: LoginDTO) {
        TODO("Not yet implemented")
    }

    override fun get() {
        TODO("Not yet implemented")
    }

}