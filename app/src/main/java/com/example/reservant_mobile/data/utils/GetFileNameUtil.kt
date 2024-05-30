package com.example.reservant_mobile.data.utils

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.core.net.toUri

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
