package com.bettafish.flarent.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.models.Tag

@Composable
fun TagList(tags: List<Tag>, modifier: Modifier = Modifier) {
    Row(modifier = modifier.height(IntrinsicSize.Min)) {
        tags.forEachIndexed { index, tag ->
            val shape = when {
                tags.size == 1 -> RoundedCornerShape(4.dp)
                index == 0 -> RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
                index == tags.lastIndex -> RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                else -> RoundedCornerShape(0.dp)
            }
            TagBadge(tag, shape = shape, modifier = Modifier.fillMaxHeight())
        }
    }
}

@Preview
@Composable
fun TagListPreview(){
    TagList(
        listOf(Tag().apply {
            name = "Windows"
            slug = "ai"
            icon = "Microsoft, your potential, our passion."
            description = "关于AI的最新动态"
            color = "#0077C8"
        },
            Tag().apply {
                name = "Beta"
                slug = "ai"
                icon = "fas fa-desktop"
                description = "关于AI的最新动态"
                color = "#00A4EF"
            }))
}