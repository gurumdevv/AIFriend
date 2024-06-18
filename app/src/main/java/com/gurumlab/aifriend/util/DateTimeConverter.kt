package com.gurumlab.aifriend.util

import android.content.res.Resources
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeConverter {

    private const val DATE_YEAR_MONTH_DAY_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"
    private val currentLocal: Locale
        get() {
            return Resources.getSystem().configuration.locales.get(0)
        }

    fun getCurrentDateString(): String {
        val date = Date()
        return SimpleDateFormat(DATE_YEAR_MONTH_DAY_TIME_PATTERN, currentLocal).format(date)
    }

    fun convertToDateTime(dateTimeString: String): Date? {
        return try {

            return SimpleDateFormat(DATE_YEAR_MONTH_DAY_TIME_PATTERN, currentLocal).parse(
                dateTimeString
            )
        } catch (e: ParseException) {
            null
        }
    }
}