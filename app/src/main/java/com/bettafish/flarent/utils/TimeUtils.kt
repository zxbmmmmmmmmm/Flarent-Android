package com.bettafish.flarent.utils

import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val ZonedDateTime.relativeTime : String
    get(){
        val now = ZonedDateTime.now()
        val duration = Duration.between(this, now)

        val days = duration.toDays()

        return when {
            duration.isNegative -> "刚刚"
            duration.toMinutes() < 1 -> "刚刚"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} 分钟前"
            duration.toHours() < 24 -> "${duration.toHours()} 小时前"
            days < 30 -> "$days 天前"
            else -> {
                if (this.year == now.year) {
                    this.format(DateTimeFormatter.ofPattern("MM-dd"))
                } else {
                    this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                }
            }
        }
    }