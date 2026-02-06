package com.bettafish.flarent.utils

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.mikepenz.markdown.model.ImageData
import com.mikepenz.markdown.model.ImageTransformer

class ClickableCoil3ImageTransformer(val click: (String)-> Unit) : ImageTransformer {

    @Composable
    override fun transform(link: String): ImageData {
        return rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(link)
                .size(coil3.size.Size.ORIGINAL)
                .build()
        ).let { painter ->
            ImageData(
                painter,
                modifier = Modifier.clickable {
                    click(link)
                }
            )
        }
    }
    @Composable
    override fun intrinsicSize(painter: Painter): Size {
        var size by remember(painter) { mutableStateOf(painter.intrinsicSize) }
        if (painter is AsyncImagePainter) {
            val painterState = painter.state.collectAsState()
            val intrinsicSize = painterState.value.painter?.intrinsicSize
            intrinsicSize?.also { size = it }
        }
        return size
    }
}