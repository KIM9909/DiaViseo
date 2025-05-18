package com.example.diaviseo.viewmodel.goal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import org.json.JSONObject
import kotlin.collections.isNullOrEmpty


class GoalViewModel : ViewModel() {
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _showDatePicker = MutableStateFlow(false)
    val showDatePicker: StateFlow<Boolean> = _showDatePicker

    private val _nutritionFeedback = MutableStateFlow("")
    val nutritionFeedback: StateFlow<String> = _nutritionFeedback

    private val _isNutriLoading = MutableStateFlow(false)
    val isNutriLoading: StateFlow<Boolean> = _isNutriLoading

    private val _workoutFeedback = MutableStateFlow("")
    val workoutFeedback: StateFlow<String> = _workoutFeedback

    private val _isWorkLoading = MutableStateFlow(false)
    val isWorkLoading: StateFlow<Boolean> = _isWorkLoading

    private val _weightFeedback = MutableStateFlow("")
    val weightFeedback: StateFlow<String> = _weightFeedback

    private val _isWeightLoading = MutableStateFlow(false)
    val isWeightLoading: StateFlow<Boolean> = _isWeightLoading

    fun loadDataForDate(date: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedDate.value = date
            _isLoading.value = false
        }
    }

    fun setShowDatePicker() {
        _showDatePicker.value = !_showDatePicker.value
    }

    // 피드백 여부
    fun isThereFeedback(
        feedbackType: String,
        date: String
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.chatBotApiService.fetchFeedBack(feedbackType, date)
                if (response.isSuccessful && response.body() != null) {
                    val message = response.body()
                    if (feedbackType == "nutrition") {
                        _nutritionFeedback.value = message.toString()
                    } else if (feedbackType == "workout") {
                        _workoutFeedback.value = message.toString()
                    } else if (feedbackType == "weight_trend") {
                        _weightFeedback.value = message.toString()
                    }
                } else {
//                    val errorJson = response.errorBody()?.string()
//                    val detail = JSONObject(errorJson ?: "").optString("detail")

                    if (feedbackType == "nutrition") {
                        _nutritionFeedback.value = ""
                    } else if (feedbackType == "workout") {
                        _workoutFeedback.value = ""
                    } else if (feedbackType == "weight_trend") {
                        _weightFeedback.value = ""
                    }
//                    Log.d("AI feedback", "메세지 : $detail")
                }
            } catch (e: Exception) {
                // 네트워크 끊김, 타임아웃 등
                Log.e("AI feedback", "❌ 네트워크 오류: ${e.localizedMessage}")
            }
        }
    }

    fun createNutriFeedBack(date: String) {
        viewModelScope.launch {
            _isNutriLoading.value = true
            try {
                val response = RetrofitInstance.chatBotApiService.createNutriFeedBack(date)
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val answer = response.body()?.get("feedback")
                    Log.d("API", "답변: $answer")
                    _nutritionFeedback.value = answer.toString()
                    _isNutriLoading.value = false
                } else {
                    val errorJson = response.errorBody()?.string()
                    val detail = JSONObject(errorJson ?: "").optString("feedback")
                    _nutritionFeedback.value = "오류가 발생했습니다 다시 한번 시도해주세요🥹"
                    Log.d("AI feedback", "메세지 : $detail")
                }
            } catch (e: Exception) {
                // 네트워크 끊김, 타임아웃 등
                Log.e("AI feedback", "❌ 네트워크 오류: ${e.localizedMessage}")
            }

            _isNutriLoading.value = false
        }
    }

    fun createHomeFeedBack(date: String) {
        viewModelScope.launch {
            _isWeightLoading.value = true
            try {
                val response = RetrofitInstance.chatBotApiService.createHomeFeedBack(date)
                if (response.isSuccessful) {
                    val answer = response.body()?.get("feedback")
                    Log.d("API", "답변: $answer")
                    _weightFeedback.value = answer.toString()
                    _isWeightLoading.value = false
                } else {
                    val errorJson = response.errorBody()?.string()
                    val detail = JSONObject(errorJson ?: "").optString("feedback")
                    _weightFeedback.value = "오류가 발생했습니다 다시 한번 시도해주세요🥹"
                    Log.d("AI feedback", "메세지 : $detail")
                }
            } catch (e: Exception) {
                // 네트워크 끊김, 타임아웃 등
                Log.e("AI feedback", "❌ 네트워크 오류: ${e.localizedMessage}")
            }

            _isWeightLoading.value = false
        }
    }

}