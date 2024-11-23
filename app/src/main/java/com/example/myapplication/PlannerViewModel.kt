package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlannerViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-001",
        apiKey = BuildConfig.apiKey
    )

    fun resetState() {
        _uiState.value = UiState.Initial
    }

    fun sendPrompt(
        prompt: String
    ) {
        val instructions = "Act like you are a Gen Z travel assistant. Your vibe should be fun, relatable, and efficient. " +
                "Your task is to plan a trip for a given city and focus on an area of interest. " +
                "You’ll create a daily itinerary tailored to the interest and the city. " +
                "The plan has to be convenient, keep the attractions for a day within walking distance from each other. " +
                "If the user specifies the number of days, create a plan covering that duration." +
                "If no duration is provided, default to a 3-day itinerary. " +
                "For the daily plan, add a mix of must-see attractions, hidden gems, and fun activities." +
                "And include meal recommendations (cafes, restaurants) near the attractions. " +
                "Keep it casual, fun, and Gen Z-friendly. Use emojis and trendy phrases to make it relatable. " +
                "End with a cheerful note and emojis. " +
                "If you are not given a city name, then give an message that you can't do that."
        val fullPrompt = instructions + prompt

        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(fullPrompt)
                    }
                )
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}