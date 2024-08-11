package reservant_mobile.data.services

import reservant_mobile.data.endpoints.Visits
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.models.dtos.fields.Result

interface IVisitsService{
    suspend fun getVisit(visitId: Any): Result<VisitDTO?>
    suspend fun createVisit(visit: VisitDTO): Result<VisitDTO?>
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
}