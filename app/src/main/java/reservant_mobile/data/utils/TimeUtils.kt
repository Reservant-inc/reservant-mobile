package reservant_mobile.data.utils

import reservant_mobile.data.models.dtos.RestaurantDTO
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Locale

fun formatToDateTime(dateString: String, pattern: String): String {
    return try {
        val formatter = DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 7, true)
            .optionalEnd()
            .toFormatter(Locale.getDefault())

        val dateTime = LocalDateTime.parse(dateString, formatter)
        dateTime.format(DateTimeFormatter.ofPattern(pattern, Locale.getDefault()))
    } catch (e: Exception) {
        "" // Zwróć pusty string w przypadku błędu parsowania
    }
}

fun formatToDateTime(dateString: String): LocalDateTime {
    return try {
        return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
    } catch (e: Exception) {
        LocalDateTime.now()
    }
}

fun List<RestaurantDTO.AvailableHours>.getRestaurantOpeningTime(opening: Boolean = true): LocalTime? {
    val dayOfWeek = LocalDateTime.now().dayOfWeek.value - 1

    val time = if (opening) {
        this[dayOfWeek].from
    } else {
        this[dayOfWeek].until
    }

    return if (time != null) LocalTime.parse(time) else null

}

