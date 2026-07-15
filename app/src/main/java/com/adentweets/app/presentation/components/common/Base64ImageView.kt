package com.adentweets.app.presentation.components.common

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun Base64ImageView(
    base64: String,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Crop,
    isCircle: Boolean = false,
    size: Dp = 48.dp
) {
    var bitmap by remember(base64) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(base64) {
        if (base64.isNotBlank()) {
            withContext(Dispatchers.IO) {
                try {
                    val bytes = Base64.decode(base64, Base64.DEFAULT)
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    bitmap = bmp?.asImageBitmap()
                } catch (e: Exception) { bitmap = null }
            }
        } else { bitmap = null }
    }

    val shape = if (isCircle) CircleShape else RoundedCornerShape(12.dp)
    val finalModifier = if (isCircle) modifier.size(size).clip(CircleShape) else modifier.clip(shape)

    if (bitmap != null) {
        Image(bitmap = bitmap!!, contentDescription = null, modifier = finalModifier, contentScale = contentScale)
    } else {
        if (placeholder != null) {
            Box(modifier = finalModifier.background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) { placeholder() }
        } else {
            Box(modifier = finalModifier.background(MaterialTheme.colorScheme.surfaceVariant)) {}
        }
    }
}