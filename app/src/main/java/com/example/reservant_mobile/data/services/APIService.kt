package com.example.reservant_mobile.data.services

import android.content.Context
import android.media.session.MediaSession
import com.example.reservant_mobile.data.models.dtos.TokenDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
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


class APIServiceImpl: APIService {

    private val localService = LocalService()

    private val client = HttpClient(CIO){
        defaultRequest {
            url("http://172.21.40.127:12038")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
        install(ContentNegotiation) {
            json()
        }
        install(HttpCookies)
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.HEADERS
        }
        install(Auth) {
            bearer {
                loadTokens {
                    // Load tokens from a local storage and return them as the 'BearerTokens' instance
                    BearerTokens(localService.getBearerToken(), localService.getRefreshToken())
                }
                refreshTokens {
                    val token: TokenDTO = client.get {
                        markAsRefreshTokenRequest()
                        url("refreshToken")
                        parameter("refreshToken", localService.getRefreshToken())
                    }.body()
                    BearerTokens(
                        accessToken = token.bearerToken,
                        refreshToken = token.refreshToken
                    )
                }
            }
        }

    }
    override suspend fun get(endpoint: String): HttpResponse? {
        return try {
            client.get(endpoint)
        } catch (e: Exception){
            null
        }
    }

    override suspend fun post(obj: @Serializable Any, endpoint: String): HttpResponse? {
        return try {
            client.post(endpoint) {
                setBody(obj)
            }
        } catch (e: Exception){
            null
        }
    }
}
