package com.denicrizz.chatnusa.utils

import com.denicrizz.chatnusa.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun getCurrentTimeFormatted(): String {
    val formatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
    formatter.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    return formatter.format(Date())
}

object GeminiBuildModel {
    fun buildGeminiRequest(userText: String, waktu: String): GeminiRequest {
        return GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = userText))
                )
            )
        )
    }
}

