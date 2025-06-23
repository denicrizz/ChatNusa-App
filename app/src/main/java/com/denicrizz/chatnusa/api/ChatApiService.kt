package com.denicrizz.chatnusa.api
import com.denicrizz.chatnusa.BuildConfig
import com.denicrizz.chatnusa.model.ChatResponse
import com.denicrizz.chatnusa.model.ChatRequest
import com.denicrizz.chatnusa.model.GeminiRequest
import com.denicrizz.chatnusa.model.GeminiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatApiService {
    @POST("chat/")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse

    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    suspend fun sendChat(
        @Query("key") apiKey: String = BuildConfig.GEMINI_API_KEY,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}

