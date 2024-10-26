package reservant_mobile.data.services

import io.ktor.http.HttpStatusCode
import reservant_mobile.data.endpoints.Visits
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.models.dtos.fields.Result

interface IVisitsService{
    suspend fun getVisit(visitId: Any): Result<VisitDTO?>
    suspend fun createVisit(visit: VisitDTO): Result<VisitDTO?>
    suspend fun approveVisit(visitId: Any): Result<Boolean>
    suspend fun declineVisit(visitId: Any): Result<Boolean>

}

class VisitsService():ServiceUtil(), IVisitsService {
    override suspend fun getVisit(visitId: Any): Result<VisitDTO?> {
        val res = api.get(Visits.VisitID(visitId=visitId.toString()))
        return complexResultWrapper(res)
    }

    override suspend fun createVisit(visit: VisitDTO): Result<VisitDTO?> {
        val res = api.post(Visits(), visit)
        return complexResultWrapper(res)
    }

    override suspend fun approveVisit(visitId: Any): Result<Boolean> {
        val res = api.post(Visits.VisitID.Approve(parent = Visits.VisitID(visitId = visitId.toString())), "")
        return booleanResultWrapper(res, expectedCode = HttpStatusCode.NoContent)
    }

    override suspend fun declineVisit(visitId: Any): Result<Boolean> {
        val res = api.post(Visits.VisitID.Decline(parent = Visits.VisitID(visitId = visitId.toString())), "")
        return booleanResultWrapper(res, expectedCode = HttpStatusCode.NoContent)
    }
}