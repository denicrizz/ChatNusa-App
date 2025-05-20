package com.denicrizz.chatnusa.api
import com.denicrizz.chatnusa.model.ChatResponse
import com.denicrizz.chatnusa.model.ChatRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApiService {
    @POST("chat/")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
}

