package com.adentweets.app.domain.usecase.media

import android.net.Uri
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.repository.MediaRepository
import javax.inject.Inject

class CompressImageUseCase @Inject constructor(
    private val repo: MediaRepository
) {
    suspend operator fun invoke(uri: Uri, maxWidthPx: Int, maxKb: Int): Resource<String> =
        repo.compressAndEncodeImage(uri, maxWidthPx, maxKb)
}