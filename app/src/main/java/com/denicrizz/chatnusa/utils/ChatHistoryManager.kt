package com.denicrizz.chatnusa.utils
import android.content.Context
import com.denicrizz.chatnusa.model.Message
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ========== CHAT HISTORY MANAGER ==========

object ChatHistoryManager {
    private const val PREF_NAME = "chat_history_prefs"
    private const val KEY_MESSAGES = "messages"

    fun saveHistory(context: Context, messages: List<Message>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(messages)
        prefs.edit().putString(KEY_MESSAGES, json).apply()
    }

    fun loadHistory(context: Context): List<Message> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_MESSAGES, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Message>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearHistory(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_MESSAGES).apply()
    }

    fun exportHistory(messages: List<Message>): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return messages.joinToString("\n\n") {
            val sender = if (it.sender == "user") "Saya" else "ChatNusa"
            val date = dateFormat.format(Date(it.timestamp))
            "[$date] $sender: ${it.text}"
        }
    }
}