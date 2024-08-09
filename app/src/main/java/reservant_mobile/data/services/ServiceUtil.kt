package reservant_mobile.data.services

import androidx.paging.PagingData
import com.example.reservant_mobile.R
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import reservant_mobile.data.models.dtos.ErrorResponseDTO
import reservant_mobile.data.models.dtos.fields.Result

abstract class ServiceUtil(protected var api: APIService = APIService()) {

    protected suspend inline fun <reified T> complexResultWrapper(res: Result<HttpResponse?>, expectedCode: HttpStatusCode = HttpStatusCode.OK): Result<T?> {
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == expectedCode){
            return try {
                val r:T = res.value.body()
                Result(isError = false, value = r)
            }
            catch (e: Exception){
                println("SERVICE ERROR: $e")
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(true, errorCodesWrapper(res.value), null)
    }

    protected suspend fun booleanResultWrapper(res: Result<HttpResponse?>, expectedCode: HttpStatusCode = HttpStatusCode.OK): Result<Boolean> {
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = false)

        if (res.value!!.status == expectedCode)
            return Result(isError = false, value = true)

        return Result(true, errorCodesWrapper(res.value), false)
    }

    protected suspend inline fun <reified T : Any>  pagingResultWrapper(sps : ServicePagingSource<T>): Result<Flow<PagingData<T>>?> {
        val flow = sps.getFlow()
        return if(flow != null)
            Result(isError = false, value = flow)
        else{
            val r = sps.getErrorResult()
            if(r != null){
                Result(isError = true, errors = r.value?.let { errorCodesWrapper(it) },value = null)
            }
            Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
        }
    }

        suspend fun errorCodesWrapper(res: HttpResponse): Map<String, Int> {
        return try {
            val errorResponse:ErrorResponseDTO = res.body()
            val map = HashMap<String, Int>()
            for(e in errorResponse.errorCodes){
                val stringId = getStringIdByPropName(e.value?.first() ?: "")
                if(stringId != -1){
                    map[e.key] = stringId
                }
            }
            map
        } catch (e: Exception) {
            println("JSON PARSING ERROR: $e")
            mapOf("TOAST" to R.string.error_unknown)
        }
    }

    private fun getStringIdByPropName(resourceName: String): Int {
        return try {
            R.string::class.java.getField("errorCode_$resourceName").getInt(null)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }
}