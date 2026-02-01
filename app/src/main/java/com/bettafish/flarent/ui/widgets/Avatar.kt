package com.bettafish.flarent.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun Avatar(avatarUrl: String? = null, name: String? = null, modifier: Modifier = Modifier) {
    if(avatarUrl != null){
        return AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(avatarUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
    if(name != null){
        return Surface (shape=CircleShape,
            color = colorScheme.primaryContainer,
            modifier = modifier){
            Box(contentAlignment = Alignment.Center) {
                Text(text = name.take(2))
            }
        }
    }
}

@Preview
@Composable
fun AvatarPicPreview(){
    Avatar("https://avatars.githubusercontent.com/u/96322503?v=4&size=64",
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape))

}
@Preview
@Composable
fun AvatarNamePreview(){
    Avatar(name="SB", modifier = Modifier.size(40.dp))
}
