package dev.mk.translatorapp

// DeepSeekApiService.kt
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DeepSeekApiService {
    @POST("chat/completions")
    suspend fun translate(@Body request: DeepSeekRequest): Response<DeepSeekResponse>
}