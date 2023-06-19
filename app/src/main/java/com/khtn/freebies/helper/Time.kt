package com.khtn.freebies.helper

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

object Time {
    @SuppressLint("SimpleDateFormat")
    fun formatMilliToTimeDate(milli: Long): String {
        return try {
            val simple: DateFormat = SimpleDateFormat("yyyy.MM.dd")
            val result = Date(milli)
            simple.format(result)
        } catch (e: Exception) {
            "-"
        }
    }
}