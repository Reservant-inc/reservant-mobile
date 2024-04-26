package com.example.reservant_mobile.data.utils

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract

fun getFileName(context: Context, uri: Uri): String {
    val cursor = context.contentResolver.query(
        uri, arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME), null, null, null
    )
    cursor?.use {
        if (it.moveToFirst()) {
            val fileNameIndex = it.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            if (fileNameIndex != -1) {
                return it.getString(fileNameIndex)
            }
        }
    }
    return "Nieznany plik"
}