package com.ferdifir.slowgram.presentation.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ferdifir.slowgram.data.StoryRepository

class SplashViewModel(private val repository: StoryRepository): ViewModel() {
    fun getUserToken(): LiveData<String?> {
        return repository.getLoginSession()
    }
}