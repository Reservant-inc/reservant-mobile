package reservant_mobile.data.services

import com.example.reservant_mobile.R
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import reservant_mobile.data.models.dtos.NominatumDTO
import reservant_mobile.data.models.dtos.fields.Result

interface INominatumService{
    suspend fun getLocationData(street: String, city: String, country: String = "Poland", postalCode: String? = null): Result<List<NominatumDTO>?>
}

class NominatimService: INominatumService {
    private val backendUrl= "https://nominatim.openstreetmap.org"
    private val client = HttpClient(CIO){
        defaultRequest {
            url(backendUrl)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }

    override suspend fun getLocationData(
        street: String,
        city: String,
        country: String,
        postalCode: String?
    ): Result<List<NominatumDTO>?> {
        val res = client.get("search") {
            url {
                parameters.append("street", street)
                parameters.append("city", city)
                parameters.append("country", country)
                parameters.append("format", "json")
                parameters.append("limit", "5")


                if(postalCode!= null){
                    parameters.append("postalcode", postalCode)
                }

            }
        }
        val parsedRes = APIService.responseWrapper(res)
        val errorRes: Result<List<NominatumDTO>?> = Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)

        if(parsedRes.isError)
            return Result(isError = true, errors = parsedRes.errors, value = null)

        if (parsedRes.value!!.status == HttpStatusCode.OK){
            return try {
                val r: List<NominatumDTO>? = parsedRes.value.body()
                Result(isError = false, value = r)
            }
            catch (e: Exception){
                println("NOMINATIM SERVICE ERROR: $e")
                errorRes
            }
        }
        client.close()

        return errorRes
    }


}