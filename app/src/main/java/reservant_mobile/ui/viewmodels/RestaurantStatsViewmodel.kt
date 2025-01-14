package reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.StatisticsDTO
import reservant_mobile.data.models.dtos.UserDTO
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.IUserService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.services.UserService
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.util.Locale

class RestaurantStatsViewmodel:ReservantViewModel() {
    var isLoading: Boolean by mutableStateOf(true)
    var loadingError: Boolean by mutableStateOf(false)
    var statistics: StatisticsDTO? by mutableStateOf(null)
    var user: UserDTO? by mutableStateOf(null)

    val months = getShortMonthNames()
    var years by mutableStateOf<List<Int>>(emptyList())

    private val currentLocale: Locale = Locale.getDefault()
    private var filterDateFrom: LocalDateTime? by mutableStateOf(null)
    private var filterDateUntil: LocalDateTime? by mutableStateOf(null)


    var restaurantService:IRestaurantService = RestaurantService()
    var userService:IUserService = UserService()

    private fun getShortMonthNames(): List<String> {
        val shortMonthNames = DateFormatSymbols(currentLocale).shortMonths

        return shortMonthNames.filter { it.isNotEmpty() }
    }

    private fun getYearsFromDate(dateString: String) {
        val date = LocalDateTime.parse(dateString)
        val startYear = date.year
        val currentYear = LocalDate.now().year

        years = (startYear..currentYear).toList()
    }

    private suspend fun getUserData(){
        if(user == null){
            val result = userService.getUserInfo()

            loadingError = result.isError
            user = if (!loadingError) {
                getYearsFromDate(result.value!!.registeredAt!!)
                result.value
            } else{
                null
            }
        }
    }

    fun setDatePeriod(year: String, month: String) {
        val parsedYear = year.toIntOrNull()

        if (parsedYear == null) {
            println("Parse year error: $year")
            filterDateFrom = null
            filterDateUntil = null
        }
        else{
            val monthEnum = parseMonth(month, currentLocale) ?: return

            val startDate = LocalDate.of(parsedYear, monthEnum, 1).atStartOfDay()
            val endDate = LocalDate.of(
                parsedYear,
                monthEnum,
                monthEnum.length(LocalDate.of(parsedYear, monthEnum, 1).isLeapYear)
            ).atTime(23, 59, 59)

            filterDateFrom = startDate
            filterDateUntil = endDate

            println("Date from: $filterDateFrom")
            println("Date untill: $filterDateUntil")

        }
    }

    private fun parseMonth(month: String, locale: Locale): Month? {
        val dateFormatSymbols = DateFormatSymbols(locale)

        // Get the short and full month names from the locale
        val shortMonths = dateFormatSymbols.shortMonths.filter { it.isNotEmpty() }
        val fullMonths = dateFormatSymbols.months.filter { it.isNotEmpty() }

        // Try to match the input month with short or full month names
        return Month.entries.firstOrNull { monthEnum ->
            month.equals(shortMonths[monthEnum.ordinal], ignoreCase = true) ||
                    month.equals(fullMonths[monthEnum.ordinal], ignoreCase = true)
        }
    }

    fun getAllStatistics() {
        viewModelScope.launch {
            isLoading = true
            val result = restaurantService.getAllStatistics(
                dateFrom = filterDateFrom,
                dateUntil = filterDateUntil
            )

            loadingError = result.isError
            statistics = if (!loadingError) {
                result.value
            } else{
                null
            }
            getUserData()
            isLoading = false

        }
    }
    fun getStatistics(restaurantId: Any){
        viewModelScope.launch {
            isLoading = true
            val result = restaurantService.getStatistics(
                restaurantId = restaurantId,
                dateFrom = filterDateFrom,
                dateUntil = filterDateUntil
            )

            loadingError = result.isError
            statistics = if (!loadingError) {
                result.value
            } else{
                null
            }
            getUserData()
            isLoading = false

        }
    }
    fun getStatisticsGroup(groupId: Any){
        viewModelScope.launch {
            isLoading = true
            val result = restaurantService.getStatisticsGroup(
                groupId = groupId,
                dateFrom = filterDateFrom,
                dateUntil = filterDateUntil
            )

            loadingError = result.isError
            statistics = if (!loadingError) {
                result.value
            } else{
                null
            }
            getUserData()
            isLoading = false

        }
    }

}