package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.fields.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.accept
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable


class APIService{

    private val backendUrl= "http://172.21.40.127:12038"

    private val localService = LocalBearerService()
    private val client = HttpClient(CIO){
        defaultRequest {
            url(backendUrl)
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
                    getBearerTokens()
                }

                refreshTokens {
                    getBearerTokens()
                }
            }
        }
        install(Resources)

    }

    private suspend inline fun getBearerTokens():BearerTokens {
        return BearerTokens(
            localService.getBearerToken(),
            ""
        )
    }

    suspend fun clearToken(){
        try{
            localService.saveBearerToken("")

            client.plugin(Auth).providers
                .filterIsInstance<BearerAuthProvider>()
                .first().clearToken()
        }
        catch (e: Exception){
            println("[TOKEN ERROR]: "+e.message)
        }


    }

    suspend inline fun <reified T : Any> get(resource: T): Result<HttpResponse?> {
        return responseWrapper(
            try {
                getHttpClient().get(resource)
            } catch (e: Exception){
                println("[GET ERROR]: "+e.message)
                null
            }
        )
    }
    suspend inline fun <reified T : Any> post(resource: T, obj: @Serializable Any): Result<HttpResponse?> {
        return responseWrapper(
            try {
                getHttpClient().post(resource) {
                    setBody(obj)
                }
            } catch (e: Exception){
                println("[POST ERROR]: "+e.message)
                null
            }
        )
    }

    suspend inline fun <reified T : Any> put(resource: T, obj: @Serializable Any): Result<HttpResponse?> {
        return responseWrapper(
            try {
                getHttpClient().put(resource) {
                setBody(obj)
            }
            } catch (e: Exception){
                println("[PUT ERROR]: "+e.message)
                null
            }
        )
    }

    suspend inline fun <reified T : Any> delete(resource: T): Result<HttpResponse?> {
        return responseWrapper(
            try {
                getHttpClient().delete(resource)
            } catch (e: Exception){
                println("[DELETE ERROR]: "+e.message)
                null
            }
        )
    }

    fun getHttpClient(): HttpClient {
        return client
    }

    suspend fun responseWrapper(res: HttpResponse?): Result<HttpResponse?> {
        res?:
            return Result(isError = true, errors =  mapOf(pair= Pair("TOAST", R.string.error_connection_server)), value = null)

        if (res.status == HttpStatusCode.Unauthorized)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = null)

        if (res.status == HttpStatusCode.NotFound)
            return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = null)

        return Result(isError = false, value = res)

    }
}
