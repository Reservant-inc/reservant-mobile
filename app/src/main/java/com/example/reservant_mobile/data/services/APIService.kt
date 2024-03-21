package com.example.reservant_mobile.data.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import java.lang.Exception

interface APIService{
    suspend fun get(endpoint: String = ""): HttpResponse?
    suspend fun post(obj: @Serializable Any, endpoint: String = ""): HttpResponse?
}

const val URL_PATH = "http://172.21.40.127:12038"
class APIServiceImpl: APIService {

    private val client = HttpClient(CIO){
        install(ContentNegotiation) {
            json()
        }
        install(HttpCookies)
    }
    override suspend fun get(endpoint: String): HttpResponse? {
        return try {
            val res = client.get(URL_PATH+endpoint)

    //        TODO: implement better logging system
            println("[GET]$endpoint body: "+ res.body())

            res
        } catch (e: Exception){
            println("[GET]$endpoint error: "+ e.message)
            null
        }
    }

    override suspend fun post(obj: @Serializable Any, endpoint: String): HttpResponse? {
        return try {
            val res =  client.post(URL_PATH+endpoint) {
                contentType(ContentType.Application.Json)
                setBody(obj)
            }

    //        TODO: implement better logging system
            println("[POST]$endpoint body: "+ res.body())

            res.body()
        } catch (e: Exception){
            println("[POST]$endpoint error: "+ e.message)
            null
        }
    }
}
