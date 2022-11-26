package com.ferdifir.slowgram.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import com.ferdifir.slowgram.data.StoryRepository
import com.ferdifir.slowgram.di.Injection
import com.ferdifir.slowgram.presentation.ui.auth.AuthViewModel
import com.ferdifir.slowgram.presentation.ui.discover.HomeViewModel
import com.ferdifir.slowgram.presentation.ui.explore.MapsViewModel
import com.ferdifir.slowgram.presentation.ui.splash.SplashViewModel
import com.ferdifir.slowgram.presentation.ui.upload.UploadViewModel

class ViewModelFactory private constructor(
    private val repository: StoryRepository
): ViewModelProvider.NewInstanceFactory(){

    @OptIn(ExperimentalPagingApi::class)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
                UploadViewModel(repository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}