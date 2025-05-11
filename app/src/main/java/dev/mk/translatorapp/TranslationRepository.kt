package dev.mk.translatorapp

// TranslationRepository.kt
import android.annotation.SuppressLint
import android.os.Build
import android.view.translation.TranslationRequest
import androidx.annotation.RequiresApi
import retrofit2.Response

class TranslationRepository {
    private val apiService = ApiClient.create()

    suspend fun translate(text: String): Response<DeepSeekResponse> {
        return apiService.translate(
            DeepSeekRequest(
                model = "deepseek/deepseek-chat-v3-0324:free",
                messages = listOf(
                    Message(
                        role = "user",
                        content = text
                    )
                ),
                temperature = 0.5,
                max_tokens = 1000
            )
        )
    }


}