package reservant_mobile.data.services

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import reservant_mobile.data.models.dtos.PageDTO
import reservant_mobile.data.models.dtos.fields.Result

class ServicePagingSource<T:Any>(
    private val fetchResult: suspend (page: Int, perPage: Int) -> Result<HttpResponse?>,
    private val serializer: KSerializer<PageDTO<T>>
) : PagingSource<Int, T>() {

    private val _pageSize = 3
    private var _hasError = false
    private lateinit var _errorRes:Result<HttpResponse?>

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val currentPage = params.key ?: 0
            val res = fetchResult(currentPage, _pageSize)


            if(res.isError || res.value!!.status != HttpStatusCode.OK){
                _errorRes = res
                throw Exception()
            }

            val jsonElement = Json.parseToJsonElement(res.value.bodyAsText())
            val page: PageDTO<T> = Json.decodeFromJsonElement(serializer, jsonElement)
            _hasError = false
            LoadResult.Page(
                data = page.items,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (currentPage < page.totalPages-1) currentPage + 1 else null
            )
        } catch (exception: Exception) {
            _hasError = true
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return null
    }


    fun getErrorResult(): Result<HttpResponse?>?{
        if( this::_errorRes.isInitialized  && _errorRes.isError)
            return _errorRes

        return null
    }

    fun getFlow(): Flow<PagingData<T>>?{
        return if(_hasError)
            null
        else
            Pager(
                PagingConfig(
                    pageSize = _pageSize,
                    prefetchDistance = 10,
                    enablePlaceholders = false)) {
                this
            }.flow

        }
}