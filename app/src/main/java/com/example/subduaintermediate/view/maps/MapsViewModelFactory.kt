package com.example.subduaintermediate.view.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.subduaintermediate.repository.StoryRepository

class MapsViewModelFactory(private val repository: StoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapsViewModel(repository) as T
    }
}
