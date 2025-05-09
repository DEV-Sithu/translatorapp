package dev.mk.translatorapp

// TranslationRepository.kt
import retrofit2.Response

class TranslationRepository {
    private val apiService = ApiClient.create()

    suspend fun translateMMToEng(text: String): Response<DeepSeekResponse> {
        return apiService.translate(
            DeepSeekRequest(
                model = "deepseek/deepseek-chat-v3-0324:free",
                messages = listOf(
                    Message(
                        role = "user",
                        content = "Translate to English : $text"
                    )
                ),
                temperature = 0.5,
                max_tokens = 1000
            )
        )
    }

}