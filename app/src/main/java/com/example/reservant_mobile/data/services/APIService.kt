package com.example.reservant_mobile.data.services

import kotlinx.serialization.Serializable

interface APIService{
    fun get(endpoint: String = "")
    fun post(obj: Serializable, endpoint: String = "")
}

class APIServiceImpl: APIService {
    override fun post(
        obj: Serializable,
        endpoint: String
    ) {
        TODO("Not yet implemented")
    }

    override fun get(
        endpoint: String
    ) {
        TODO("Not yet implemented")
    }

}