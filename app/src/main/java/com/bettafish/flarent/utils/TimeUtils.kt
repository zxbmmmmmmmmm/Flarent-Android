package com.bettafish.flarent.utils

import com.bettafish.flarent.App
import com.bettafish.flarent.R
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val ZonedDateTime.relativeTime: String
    get() {
        val now = ZonedDateTime.now()
        val duration = Duration.between(this, now)
        val resources = App.INSTANCE.resources

        val days = duration.toDays()

        return when {
            duration.isNegative -> resources.getString(R.string.relative_time_just_now)
            duration.toMinutes() < 1 -> resources.getString(R.string.relative_time_just_now)
            duration.toMinutes() < 60 -> {
                val minutes = duration.toMinutes().toInt()
                resources.getQuantityString(R.plurals.relative_time_minutes_ago, minutes, minutes)
            }
            duration.toHours() < 24 -> {
                val hours = duration.toHours().toInt()
                resources.getQuantityString(R.plurals.relative_time_hours_ago, hours, hours)
            }
            days < 30 -> {
                val daysCount = days.toInt()
                resources.getQuantityString(R.plurals.relative_time_days_ago, daysCount, daysCount)
            }
            else -> {
                if (this.year == now.year) {
                    this.format(DateTimeFormatter.ofPattern("MM-dd"))
                } else {
                    this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                }
            }
        }
    }
