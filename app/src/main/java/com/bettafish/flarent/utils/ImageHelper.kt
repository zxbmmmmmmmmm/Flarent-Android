package com.bettafish.flarent.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ImageHelper(private val context: Context) {

    private val imageLoader: ImageLoader = SingletonImageLoader.get(context)

    /**
     * 保存图片到 Download 文件夹
     */
    suspend fun saveImageToDownloads(imageUrl: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            // 1. 获取输入流 (优先读取缓存)
            val (inputStream, extension) = getInputStreamFromCoil(imageUrl)
                ?: throw Exception("Failed to fetch image")

            inputStream.use { input ->
                val fileName = "IMG_${System.currentTimeMillis()}.$extension"
                val mimeType = getMimeType(extension)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+ (API 29+) 使用 MediaStore.Downloads
                    saveToMediaStoreDownloads(input, fileName, mimeType)
                } else {
                    // Android 9- 使用传统文件系统
                    saveToLegacyDownloads(input, fileName)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 分享图片
     * @param imageUrl 图片链接
     */
    suspend fun shareImage(imageUrl: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val (inputStream, extension) = getInputStreamFromCoil(imageUrl) ?: throw Exception("Failed to fetch image")

            // 将文件保存到应用的私有缓存目录，以便安全分享
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "share_temp.$extension")

            inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = getMimeType(extension)
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // 需在 UI 线程启动 Activity，这里返回成功让 UI 层处理，或者使用 context.startActivity (需 FLAG_ACTIVITY_NEW_TASK)
            val chooser = Intent.createChooser(intent, "Share Image")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveToMediaStoreDownloads(input: InputStream, fileName: String, mimeType: String): Result<String> {
        // Android 10 (Q) 引入了 MediaStore.Downloads
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return Result.failure(Exception("API level too low for MediaStore.Downloads"))
        }

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI

        val uri = context.contentResolver.insert(collection, values)
            ?: return Result.failure(Exception("MediaStore insert failed"))

        return try {
            context.contentResolver.openOutputStream(uri)?.use { output ->
                input.copyTo(output)
            }
            Result.success("Saved to Downloads")
        } catch (e: Exception) {
            context.contentResolver.delete(uri, null, null)
            Result.failure(e)
        }
    }

    private fun saveToLegacyDownloads(input: InputStream, fileName: String): Result<String> {
        val targetDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!targetDir.exists()) targetDir.mkdirs()

        val targetFile = File(targetDir, fileName)

        return try {
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
            // 扫描文件使其在文件管理器中可见
            MediaScannerConnection.scanFile(context, arrayOf(targetFile.absolutePath), null, null)
            Result.success(targetFile.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getMimeType(extension: String): String {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase()) ?: "image/jpeg"
    }

    private suspend fun getInputStreamFromCoil(url: String): Pair<InputStream, String>? {
        imageLoader.diskCache?.openSnapshot(url)?.use { snapshot ->
            val ext = MimeTypeMap.getFileExtensionFromUrl(url).ifEmpty { "jpg" }
            return Pair(snapshot.data.toFile().inputStream(), ext)
        }

        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .build()

        val result = imageLoader.execute(request)
        if (result is SuccessResult) {
            imageLoader.diskCache?.openSnapshot(url)?.use { snapshot ->
                val ext = MimeTypeMap.getFileExtensionFromUrl(url).ifEmpty { "jpg" }
                return Pair(snapshot.data.toFile().inputStream(), ext)
            }
            // Fallback for bitmap
            val bitmap = result.image.toBitmap()
            val outputStream = java.io.ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            return Pair(java.io.ByteArrayInputStream(outputStream.toByteArray()), "jpg")
        }
        return null
    }
}