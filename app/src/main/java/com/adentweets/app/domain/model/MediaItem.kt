package com.adentweets.app.domain.model

data class MediaItem(
    val mediaId: String = "",
    val type: MediaType = MediaType.IMAGE,
    val base64Data: String = "",
    val mimeType: String = "image/jpeg",
    val width: Int = 0,
    val height: Int = 0,
    val durationMs: Long = 0L,
    val thumbnailBase64: String = ""
)

enum class MediaType { IMAGE, VIDEO, GIF }