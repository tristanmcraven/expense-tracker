package com.tristanmcraven.expensetracker.utility

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.tristanmcraven.expensetracker.ExpenseTrackerApp
import java.io.File
import java.io.FileOutputStream

object GenericHelper {

    var MainCurrencySymbol = "$"

    suspend fun saveImageToAppStorage(context: Context, sourceUri: Uri): Uri {

        // create subfolder
        val avatarDir = File(context.filesDir, "avatars").apply { if (!exists()) mkdirs() }

        val extension = context.contentResolver.getType(sourceUri)
            ?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
            ?: ".jpg"
        val destinationFile = File(avatarDir, "avatar_${System.currentTimeMillis()}.$extension")

        context.contentResolver.openInputStream(sourceUri).use { input ->
            FileOutputStream(destinationFile).use { output ->
                input?.copyTo(output)
            }
        }

        return Uri.fromFile(destinationFile)
    }

    //TODO implement it in SettingsFragment
    suspend fun getCurrentDbVersion(context: Context): Int {
        val db = (context.applicationContext as ExpenseTrackerApp).db
        val sqliteDb = db.openHelper.writableDatabase
        return sqliteDb.version
    }
}