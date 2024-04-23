package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.TokenDTO
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.ui.constants.Endpoints
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlin.Exception

interface APIService{
    suspend fun get(endpoint: String = ""): Result<HttpResponse?>
    suspend fun post(obj: @Serializable Any, endpoint: String = ""): Result<HttpResponse?>
    suspend fun put(obj: @Serializable Any, endpoint: String): Result<HttpResponse?>
    suspend fun delete(endpoint: String): Result<HttpResponse?>
    suspend fun getHttpClient(): HttpClient
    suspend fun responseWrapper(res: HttpResponse?): Result<HttpResponse?>
}


class APIServiceImpl: APIService {

    private val localService = LocalBearerService()

    private val client = HttpClient(CIO){
        defaultRequest {
            url(Endpoints.BACKEND_URL)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
        install(ContentNegotiation) {
            json()
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(
                        localService.getBearerToken(),
                        localService.getRefreshToken()
                    )
                }
            }
        }

    }


    override suspend fun get(endpoint: String): Result<HttpResponse?> {
        return responseWrapper(
            try {
                client.get(endpoint)
            } catch (e: Exception){
                println("[GET ERROR]: "+e.message)
                null
            }
        )
    }

    override suspend fun post(obj: @Serializable Any, endpoint: String): Result<HttpResponse?> {
        return responseWrapper(
            try {
                client.post(endpoint) {
                    setBody(obj)
                }
            } catch (e: Exception){
                println("[POST ERROR]: "+e.message)
                null
            }
        )
    }

    override suspend fun put(obj: @Serializable Any, endpoint: String): Result<HttpResponse?> {
        return responseWrapper(
            try {
                client.put(endpoint) {
                setBody(obj)
            }
            } catch (e: Exception){
                println("[PUT ERROR]: "+e.message)
                null
            }
        )
    }

    override suspend fun delete(endpoint: String): Result<HttpResponse?> {
        return responseWrapper(
            try {
                client.delete(endpoint)
            } catch (e: Exception){
                println("[DELETE ERROR]: "+e.message)
                null
            }
        )
    }

    override suspend fun getHttpClient(): HttpClient {
        return client
    }

    override suspend fun responseWrapper(res: HttpResponse?): Result<HttpResponse?> {
        res?:
            return Result(isError = true, errors =  mapOf(pair= Pair("TOAST", R.string.error_connection_server)), value = null)

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = null)

        if (res.status == HttpStatusCode.BadRequest)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = null)

        if (res.status == HttpStatusCode.NotFound)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = null)

        return Result(isError = false, value = res)

    }
}
