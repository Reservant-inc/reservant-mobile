package reservant_mobile.data.services

import com.example.reservant_mobile.R
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import reservant_mobile.data.endpoints.Visits
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.models.dtos.fields.Result

interface IVisitsService{
    suspend fun getVisit(visitId: Any): Result<VisitDTO?>
    suspend fun createVisit(visit: VisitDTO): Result<VisitDTO?>
}

class VisitsService(private var api: APIService = APIService()): IVisitsService {
    override suspend fun getVisit(visitId: Any): Result<VisitDTO?> {
        val res = api.get(Visits.VisitID(visitId=visitId.toString()))

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }
        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    override suspend fun createVisit(visit: VisitDTO): Result<VisitDTO?> {
        val res = api.post(Visits(), visit)

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }
        return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }
}