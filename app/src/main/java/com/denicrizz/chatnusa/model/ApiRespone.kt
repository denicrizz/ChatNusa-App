package com.denicrizz.chatnusa.model

// ========== DATA CLASSES ==========

data class Message(val sender: String, val text: String, val timestamp: Long = System.currentTimeMillis())
data class ChatRequest(val message: String)
data class ChatResponse(val type: String, val results: List<ResearchPaper>?, val jawaban: String?)
data class ResearchPaper(val title: String, val link: String, val score: Double)

// GEMINI AI
data class Part(val text: String)
data class Content(val parts: List<Part>)
data class GeminiRequest(val contents: List<Content>)

data class PartResponse(val text: String)
data class ContentResponse(val parts: List<PartResponse>)
data class Candidate(val content: ContentResponse)
data class GeminiResponse(val candidates: List<Candidate>)
