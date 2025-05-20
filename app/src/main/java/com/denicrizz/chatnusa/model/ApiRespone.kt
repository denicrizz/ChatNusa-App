package com.denicrizz.chatnusa.model

// ========== DATA CLASSES ==========

data class Message(val sender: String, val text: String, val timestamp: Long = System.currentTimeMillis())
data class ChatRequest(val message: String)
data class ChatResponse(val type: String, val results: List<ResearchPaper>?, val jawaban: String?)
data class ResearchPaper(val title: String, val link: String, val score: Double)