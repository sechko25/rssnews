package com.example.yetanotherfeed.utils

import android.content.res.Resources
import com.example.yetanotherfeed.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private val ONE_MINUTE_MILLIS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES)
private val ONE_HOUR_MILLIS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
private val ONE_DAY_MILLIS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)


fun convertMillisToFormatted(pubDateMilli: Long, res: Resources): String {
    val durationMilli = System.currentTimeMillis() - pubDateMilli
    return when {
        durationMilli < ONE_MINUTE_MILLIS -> {
            val seconds = TimeUnit.SECONDS.convert(durationMilli, TimeUnit.MILLISECONDS)
            res.getString(R.string.seconds_length, seconds)
        }
        durationMilli < ONE_HOUR_MILLIS -> {
            val minutes = TimeUnit.MINUTES.convert(durationMilli, TimeUnit.MILLISECONDS)
            res.getString(R.string.minutes_length, minutes)
        }
        durationMilli < ONE_DAY_MILLIS -> {
            val hours = TimeUnit.HOURS.convert(durationMilli, TimeUnit.MILLISECONDS)
            res.getString(R.string.hours_length, hours)
        }
        else -> {
            SimpleDateFormat("MMMM dd HH:mm", Locale.getDefault()).format(pubDateMilli)
                .toString()
        }
    }
}