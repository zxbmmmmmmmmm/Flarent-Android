package com.bettafish.flarent.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.models.Notification
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.User
import com.bettafish.flarent.utils.relativeTime
import java.time.ZonedDateTime

@Composable
fun NotificationItem(notification: Notification, modifier: Modifier = Modifier){
    Row(modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)){
        Avatar(
            avatarUrl = notification.fromUser?.avatarUrl,
            name = notification.fromUser?.displayName,
            modifier = Modifier.height(40.dp).width(40.dp).clip(CircleShape),
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = notification.fromUser?.displayName ?: notification.fromUser?.username ?: "",
                    color = MaterialTheme.colorScheme.primary)
                Text(
                    text = notification.createdAt?.relativeTime ?: "",
                    color = MaterialTheme.colorScheme.outline)
            }
            Text(
                fontWeight = FontWeight.SemiBold,
                text = when(notification.contentType){
                    "post" -> "mentioned you in a post"
                    "comment" -> "mentioned you in a comment"
                    else -> "sent you a notification"
                }
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun NotificationItemPreview(){
    NotificationItem(notification = Notification().apply{
        id = "1"
        contentType = "post"
        content = "This is a notification content"
        isRead = false
        createdAt = ZonedDateTime.now()
        fromUser = User().apply {
            displayName = "John Doe"
            username = "AA"
            id = "1"
        }
        subject = Post().apply {
            id = "1"
            content = "This is a post content"
        }
    })
}
