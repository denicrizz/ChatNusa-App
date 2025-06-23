package com.denicrizz.chatnusa.api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.denicrizz.chatnusa.BuildConfig

object RetrofitClient {
    private const val BASE_URL = "https://bot-api.zpedia.eu.org/api/"
    private const val GEMINI_URL = "https://generativelanguage.googleapis.com/"

    val api: ChatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApiService::class.java)
    }

    val geminiApi: ChatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(GEMINI_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApiService::class.java)
    }
}