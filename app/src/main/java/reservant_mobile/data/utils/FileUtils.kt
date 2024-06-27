package reservant_mobile.data.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toUri
import java.io.ByteArrayOutputStream
import java.util.UUID

fun getFileName(context: Context, uriOrFileName: String): String {
    if (uriOrFileName.startsWith("content://") || uriOrFileName.startsWith("file://")) {
        val uri = uriOrFileName.toUri()
        var fileName: String? = null

        if (uri.scheme == "content") {
            context.contentResolver.query(
                uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val fileNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (fileNameIndex != -1) {
                        fileName = cursor.getString(fileNameIndex)
                    }
                }
            }
        }

        if (fileName == null) {
            fileName = uri.path
            val cut = fileName?.lastIndexOf('/')
            if (cut != -1 && cut != null) {
                fileName = fileName?.substring(cut + 1)
            }
        }

        return fileName ?: "Nieznany plik"
    }

    // Jeśli wartość nie jest URI, zwracamy ją bez zmian
    return uriOrFileName
}

fun getFileFromUri(context: Context, uri: Uri): ByteArray? {
    val contentResolver: ContentResolver = context.contentResolver
    val outputStream = ByteArrayOutputStream()

    try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val buffer = ByteArray(4 * 1024) // buffer size
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    return outputStream.toByteArray()
}

fun isFileNameInvalid(uri: String?): Boolean {
    if (uri.isNullOrEmpty()) return true
    val fileName = uri.substringAfterLast('/')
    return !fileName.contains(".") || fileName.substringAfterLast(".").isEmpty()
}

fun isUuid(name: String): Boolean {
    return try {
        UUID.fromString(name)
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}

fun getFileNameWithoutExtension(name: String): String {
    val lastDotIndex = name.lastIndexOf('.')
    return if (lastDotIndex != -1) {
        name.substring(0, lastDotIndex)
    } else {
        name
    }
}

fun isFileSizeInvalid(context: Context, uri: String?): Boolean {
    if (uri == null) {
        return false
    }

    val fileNameWithoutExtension = getFileNameWithoutExtension(uri)

    if (isUuid(fileNameWithoutExtension)) {
        return false
    }

    val byteArray = getFileFromUri(context, uri.toUri())

    return (byteArray?.size ?: 0) > 1024000
}