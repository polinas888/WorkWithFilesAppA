package com.example.workwithfilesappa

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class StorageUtils {
    companion object {
        val minSdk29: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        fun updateOrRequestReadPermission(
            context: Context,
            readPermissionGranted: Boolean,
            permissionsLauncher: ActivityResultLauncher<Array<String>>
        ): Boolean {
            val hasReadPermission = ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            val permissionsToRequest = mutableListOf<String>()
            if (!readPermissionGranted)
                permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permissionsToRequest.isNotEmpty())
                permissionsLauncher.launch(permissionsToRequest.toTypedArray())
            return hasReadPermission
        }

        fun updateOrRequestWritePermission(
            context: Context,
            writePermissionGranted: Boolean,
            permissionsLauncher: ActivityResultLauncher<Array<String>>
        ): Boolean {
            val hasWritePermission = ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            val permissionsToRequest = mutableListOf<String>()
            if (!writePermissionGranted)
                permissionsToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permissionsToRequest.isNotEmpty())
                permissionsLauncher.launch(permissionsToRequest.toTypedArray())
            return hasWritePermission
        }

        fun getImageCollectionUri() = sdk29AndUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        private inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                onSdk29()
            } else null
        }

        @Throws(IOException::class)
        fun readFromUri(context: Context, uri: Uri): String {
            val stringBuilder = StringBuilder()
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line)
                        line = reader.readLine()
                    }
                }
            }
            return stringBuilder.toString()
        }

        fun writeToFile(content: String, context: Context, uri: Uri) {
            try {
                val outputStream = context.contentResolver.openOutputStream(uri)
                outputStream?.write(content.toByteArray())
                outputStream?.close()
            } catch (e: java.lang.Exception) {
                Log.i("FileWriter", "Cant write the file")
            }
        }
    }
}