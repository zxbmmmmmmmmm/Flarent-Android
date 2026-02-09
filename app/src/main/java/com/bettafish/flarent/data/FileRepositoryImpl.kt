package com.bettafish.flarent.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.bettafish.flarent.models.File
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.network.FlarumService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class FileRepositoryImpl(
    private val service: FlarumService,
    private val context: Context
): FileRepository {
    override suspend fun upload(uri: Uri): List<File> {
        return withContext(Dispatchers.IO) {
            val multipartBody = prepareMultipartBody(uri)
                ?: throw Exception("无法解析文件路径")

            service.uploadFile(multipartBody)
        }
    }

    /**
     * 将 Uri 转换为 MultipartBody.Part
     */
    private fun prepareMultipartBody(uri: Uri): MultipartBody.Part? {
        val contentResolver = context.contentResolver

        val fileName = getFileName(uri) ?: "temp_file"
        val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

        return contentResolver.openInputStream(uri)?.use { inputStream ->
            val requestBody = inputStream.readBytes().toRequestBody(
                mimeType.toMediaTypeOrNull(),
                0
            )
            MultipartBody.Part.createFormData("files[]", fileName, requestBody)
        }
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) name = it.getString(nameIndex)
            }
        }
        return name
    }
}