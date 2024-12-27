package com.example.subduaintermediate.di

import android.content.Context
import com.example.subduaintermediate.data.api.ApiConfig
import com.example.subduaintermediate.data.preference.UserPreference
import com.example.subduaintermediate.data.preference.dataStore
import com.example.subduaintermediate.repository.StoryRepository
import com.example.subduaintermediate.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository.getInstance(apiService)
    }
}
