package com.adentweets.app.domain.repository

import android.net.Uri
import com.adentweets.app.core.util.Resource

interface MediaRepository {
    suspend fun compressAndEncodeImage(uri: Uri, maxWidthPx: Int, maxKb: Int): Resource<String>
    suspend fun encodeVideo(uri: Uri): Resource<String>
    fun decodeBase64ToBitmap(base64: String): android.graphics.Bitmap?
}