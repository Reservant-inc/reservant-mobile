package reservant_mobile.data.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Locale

fun formatDateTime(dateString: String, pattern: String): String {
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

fun formatDateTime(dateString: String): LocalDateTime {
    return try {
        val formatter = DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 7, true)
            .optionalEnd()
            .toFormatter(Locale.getDefault())

        return LocalDateTime.parse(dateString, formatter)
    } catch (e: Exception) {
        LocalDateTime.now()
    }
}