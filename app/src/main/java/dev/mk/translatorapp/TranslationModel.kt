package dev.mk.translatorapp


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TranslationViewModel : ViewModel() {
    private val repository = TranslationRepository()

    private val _translationResult = MutableLiveData<TranslationResult>()
    val translationResult: LiveData<TranslationResult> = _translationResult

    fun translateText(text: String) {
        if (text.isEmpty()) {
            _translationResult.value = TranslationResult.Error("Please enter text to translate")
            return
        }

        viewModelScope.launch {
            _translationResult.value = TranslationResult.Loading
            try {
                val response = repository.translateMMToEng(text)
                if (response.isSuccessful) {
                    val translatedText = response.body()?.choices?.firstOrNull()?.message?.content
                        ?: throw Exception("Translation failed")
                    _translationResult.value = TranslationResult.Success(translatedText)
                } else {
                    _translationResult.value = TranslationResult.Error("API Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _translationResult.value = TranslationResult.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class TranslationResult {
    data class Success(val translatedText: String) : TranslationResult()
    data class Error(val message: String) : TranslationResult()
    object Loading : TranslationResult()
}