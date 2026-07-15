package com.adentweets.app.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.adentweets.app.core.util.Resource
import com.adentweets.app.data.remote.media.FirebaseMediaSource
import com.adentweets.app.domain.repository.MediaRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val mediaSource: FirebaseMediaSource
) : MediaRepository {

    override suspend fun compressAndEncodeImage(uri: Uri, maxWidthPx: Int, maxKb: Int): Resource<String> {
        return try {
            val base64 = mediaSource.compressAndEncodeImage(uri, maxWidthPx, maxKb)
            if (base64 != null) Resource.Success(base64) else Resource.Error("Failed to compress image")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to compress image")
        }
    }

    override suspend fun encodeVideo(uri: Uri): Resource<String> {
        return try {
            val base64 = mediaSource.encodeVideo(uri)
            if (base64 != null) Resource.Success(base64) else Resource.Error("Failed to encode video")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to encode video")
        }
    }

    override fun decodeBase64ToBitmap(base64: String): Bitmap? {
        return mediaSource.decodeBase64ToBitmap(base64)
    }
}