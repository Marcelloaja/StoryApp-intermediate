package com.example.subduaintermediate.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.subduaintermediate.data.StoryPagingSource
import com.example.subduaintermediate.data.api.ApiService
import com.example.subduaintermediate.data.response.ListStoryItem
import com.example.subduaintermediate.data.response.StoryResponse

open class StoryRepository(private val apiService: ApiService) {

    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }

    suspend fun getStoriesWithLocation(location: Int = 1): StoryResponse {
        return apiService.getStoriesWithLocation(location)
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService).also { instance = it }
            }
        }
    }
}