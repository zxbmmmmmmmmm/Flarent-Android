package com.bettafish.flarent.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.models.Tag
import com.bettafish.flarent.utils.toComposeColor
import com.bettafish.flarent.utils.toFaIcon
import com.guru.fontawesomecomposelib.FaIcon

@Composable
fun TagBadge(tag: Tag,
             modifier: Modifier = Modifier,
             shape: Shape = RoundedCornerShape(4.dp),
             click: (Tag) -> Unit = {}) {

    val bgColor = remember { tag.color?.toComposeColor() } ?: colorScheme.secondaryContainer
    Surface(
        color = bgColor,
        shape = shape,
        modifier = modifier.clickable{ click(tag) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val contentColor = remember { if (bgColor.luminance() < 0.5f) Color.White else Color.Black }
            val textStyle = MaterialTheme.typography.bodySmall
            val density = LocalDensity.current
            val textHeightDp = with(density) { textStyle.lineHeight.toDp() }
            val icon = remember { tag.icon?.toFaIcon()  }
            if(icon != null){
                FaIcon(
                    faIcon = icon,
                    tint = contentColor,
                    size = textHeightDp)
            }
            Text(
                text = tag.name ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                modifier = if(icon != null) Modifier.padding(start = 4.dp) else Modifier.padding(0.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}



@Composable
@Preview
fun TagPreview(tag: Tag = Tag().apply {
    name = "Windows"
    slug = "ai"
    icon = "fas fa-windows"
    description = "Microsoft, your potential, our passion."
    color = "#0077C8"
}){
    TagBadge(tag)
}

