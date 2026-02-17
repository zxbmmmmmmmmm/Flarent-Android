package com.bettafish.flarent.ui.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.models.Reaction

@Composable
fun ReactionBadge(
    reaction: Reaction,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) {
        colorScheme.primaryContainer
    } else {
        colorScheme.surfaceContainer
    }

    val contentColor = if (selected) {
        colorScheme.onPrimaryContainer
    } else {
        colorScheme.onSurface
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = Modifier.height(36.dp),
        contentPadding = PaddingValues(4.dp,8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val emoji = if(reaction.type == "emoji" && reaction.identifier != null){
                getEmoji(reaction.identifier!!)
            }
            else{
                reaction.display ?: ""
            }
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = count.toString(),
                color = if (selected) {
                    colorScheme.primary
                }else{
                    colorScheme.outline
                }
            )
        }
    }
}

@Composable
fun ReactionList(reactions: List<Pair<Reaction,Int>>,
                 selectedReaction: String? = null,
                 modifier: Modifier = Modifier,
                 onReactionSelected: (String) -> Unit = {}){
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier){
        reactions.forEach {
            if(it.second != 0){
                ReactionBadge(it.first,
                    it.second,
                    it.first.id == selectedReaction,
                    { onReactionSelected(it.first.id) })
            }
        }
    }
}

val emojiMap = mapOf(
    "thinking" to "🤔",
    "rofl" to "🤣",
    "heart" to "❤️",
    "lemon" to "🍋",
    "tada" to "🎉",
    "herb" to "🌿",
    "savour" to "😋",
    "cold_sweat" to "😰",
    "overheating" to "🥵",
)

fun getEmoji(name: String): String {
    return emojiMap[name.lowercase()] ?: "❓"
}

@Composable
@Preview(showBackground = true)
fun ReactionBadgePreview(){
    ReactionBadge(Reaction().apply {
        identifier = "thinking"
        display = "思考"
        type = "emoji"
        enabled = true
    }, 1, false, {})
}


@Composable
@Preview(showBackground = true)
fun ReactionBadgeSelectedPreview(){
    ReactionBadge(Reaction().apply {
        identifier = "thinking"
        display = "思考"
        type = "emoji"
        enabled = true
    }, 1, true, {})
}

@Composable
@Preview(showBackground = true)
fun ReactionListPreview(){
    ReactionList(listOf(
        Pair(Reaction().apply {
            id = "1"
            identifier = "thinking"
            display = "思考"
            type = "emoji"
            enabled = true
        },2),
        Pair(Reaction().apply {
            id = "2"
            identifier = "overheating"
            display = "烧！"
            type = "emoji"
            enabled = true
        },1),
        Pair(Reaction().apply {
            id = "3"
            identifier = "herb"
            display = "草"
            type = "emoji"
            enabled = true
        },1)),"2")
}