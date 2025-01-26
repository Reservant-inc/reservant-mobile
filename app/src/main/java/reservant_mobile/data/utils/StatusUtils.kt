package reservant_mobile.data.utils

import android.content.Context
import com.example.reservant_mobile.R

object StatusUtils {
    data class StatusOption(val statusString: String, val displayNameResId: Int)

    val statusOptions = listOf(
        StatusOption("InProgress", R.string.status_in_progress),
        StatusOption("Ready", R.string.status_ready),
        StatusOption("Cancelled", R.string.status_cancelled)
    )

    fun getStatusDisplayName(status: String, context: Context): String {
        val statusOption = statusOptions.find { it.statusString == status }
        return statusOption?.let { context.getString(it.displayNameResId) } ?: status
    }
}
