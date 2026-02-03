package com.bettafish.flarent.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.models.Group
import com.bettafish.flarent.models.User
import com.bettafish.flarent.utils.relativeTime
import com.bettafish.flarent.utils.toComposeColor
import com.bettafish.flarent.utils.toFaIcon
import com.guru.fontawesomecomposelib.FaIcon
import java.time.ZonedDateTime

@Composable
fun ProfileHeader(user: User, modifier: Modifier = Modifier){
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Avatar(
            avatarUrl = user.avatarUrl,
            name = user.displayName,
            modifier = Modifier
                .width(64.dp)
                .height(64.dp)
                .clip(CircleShape)
        )
        Column() {
            Text(
                text = user.displayName ?: user.username ?: "",
                style = MaterialTheme.typography.titleLarge
            )
            if (user.displayName != null && user.username != null) {
                Text(
                    user.username!!,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Row(modifier = Modifier.padding(top = 2.dp)) {
                val isOnline =
                    user.lastSeenAt?.isAfter(ZonedDateTime.now().minusMinutes(2)) ?: false
                if (isOnline) {
                    Surface(
                        shape = CircleShape,
                        modifier = Modifier.padding(end = 4.dp).height(8.dp).width(8.dp)
                            .align(Alignment.CenterVertically),
                        color = "#00E600".toComposeColor()!!
                    ) { }
                }
                user.lastSeenAt?.let {
                    val text = if (isOnline) "在线" else "最后登录于 ${it.relativeTime}"
                    Text(
                        text,
                        modifier = Modifier.padding(end = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                user.joinTime?.let {
                    Text(
                        "注册于 ${it.relativeTime}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        if(user.groups?.size != 0){
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                user.groups!!.forEach {
                    GroupBadge(it)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)){
            val baseTextStyle = MaterialTheme.typography.bodyMedium.copy(
                platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                    includeFontPadding = false
                )
            )

            val density = LocalDensity.current
            val dividerHeight = remember(baseTextStyle.fontSize, density) {
                with(density) { baseTextStyle.fontSize.toDp() }
            }

            @Composable
            fun StatItem(label: String, value: String) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        color = MaterialTheme.colorScheme.outline,
                        style = baseTextStyle
                    )
                    Text(
                        text = value,
                        style = baseTextStyle,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            StatItem("粉丝", user.followerCount.toString())

            VerticalDivider(modifier = Modifier.height(dividerHeight).align(Alignment.CenterVertically))

            StatItem("关注", user.followingCount.toString())

            VerticalDivider(modifier = Modifier.height(dividerHeight).align(Alignment.CenterVertically))

            StatItem("获赞", user.points.toString())
        }
        if(!user.bio.isNullOrEmpty()){
            Text(user.bio!!)
        }
    }
}


@Composable
fun GroupBadge(group: Group, modifier: Modifier = Modifier){
    val bgColor = remember { group.color?.toComposeColor() } ?: MaterialTheme.colorScheme.secondaryContainer
    val contentColor = remember { if (bgColor.luminance() < 0.5f) Color.White else Color.Black }
    Surface(
        color = bgColor,
        shape = CircleShape,
        modifier = modifier
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)){
            val textStyle = MaterialTheme.typography.labelMedium
            group.icon?.let {
                val density = LocalDensity.current
                val textHeightDp = with(density) { textStyle.lineHeight.toDp() }
                val icon = it.toFaIcon()
                if(icon != null){
                    FaIcon(icon, size = textHeightDp, tint = contentColor)
                }
            }

            Text(
                text = group.nameSingular ?: "",
                style = textStyle,
                color = contentColor
            )
        }
    }}

@Preview
@Composable
fun GroupBadgePreview(){
    GroupBadge(Group().apply {
        nameSingular = "Admin"
        color = "#3A75EE"
        icon = "fas fa-wrench"
        isHidden = 0
    })
}

@Preview(showBackground = true)
@Composable
fun ProfileHeaderPreview(){
    ProfileHeader(User().apply {
        displayName = "Betta_Fish"
        username = "zxbmmmmmmmmm"
        id = "1"
        joinTime = ZonedDateTime.now()
        lastSeenAt = ZonedDateTime.now()
        followerCount = 11
        followingCount = 4
        points = 514
        bio = "这个人很懒，什么都没有写"
        groups = listOf(
            Group().apply {
            nameSingular = "Admin"
            color = "#3A75EE"
            icon = "fas fa-wrench"
            isHidden = 0
        },
            Group().apply {
                nameSingular = "User"
                color = "#763D99"
                icon = "fas fa-bolt"
                isHidden = 0
            })
    })
}
