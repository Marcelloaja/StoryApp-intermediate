package com.example.subduaintermediate.view.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.subduaintermediate.data.response.StoryResponse
import com.example.subduaintermediate.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getStoriesWithLocation() = liveData(Dispatchers.IO) {
        try {
            val response = repository.getStoriesWithLocation()
            if (response.error) {
                emit(StoryResponse(error = true, message = "Failed to fetch data", listStory = emptyList()))
            } else {
                emit(response)
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                emit(StoryResponse(error = true, message = "Unauthorized: Invalid or expired token", listStory = emptyList()))
            } else {
                emit(StoryResponse(error = true, message = "Failed to fetch data: ${e.message}", listStory = emptyList()))
            }
        }
    }
}
