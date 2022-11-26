package com.ferdifir.slowgram.di

import android.content.Context
import com.ferdifir.slowgram.data.StoryRepository
import com.ferdifir.slowgram.data.datastore.UserPreferences
import com.ferdifir.slowgram.data.datastore.UserPreferences.Companion.dataStore
import com.ferdifir.slowgram.data.local.room.StoryDatabase
import com.ferdifir.slowgram.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.provideApiService()
        val database = StoryDatabase.getInstance(context)
        val pref = UserPreferences(context.dataStore)
        return StoryRepository(pref, database, apiService)
    }
}