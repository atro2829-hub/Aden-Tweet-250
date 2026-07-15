package com.adentweets.app.data.remote.media

import com.adentweets.app.core.util.Base64Utils
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMediaSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun compressAndEncodeImage(uri: Uri, maxWidthPx: Int = 1080, maxKb: Int = 800): String? {
        return Base64Utils.compressAndEncodeImage(context, uri, maxWidthPx, maxKb)
    }

    fun encodeVideo(uri: Uri): String? {
        return Base64Utils.videoUriToBase64(context, uri)
    }

    fun decodeBase64ToBitmap(base64: String): Bitmap? {
        return Base64Utils.base64ToBitmap(base64)
    }
}