package com.ferdifir.slowgram.presentation.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ferdifir.slowgram.data.StoryRepository
import com.ferdifir.slowgram.data.remote.response.LoginResponse
import com.ferdifir.slowgram.data.remote.response.RegisterResponse
import com.ferdifir.slowgram.utils.Result

class AuthViewModel(private val repository: StoryRepository): ViewModel() {
    fun userLogin(email: String, password: String): LiveData<Result<LoginResponse>> =
        repository.userLogin(email, password)

    fun userRegister(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> =
        repository.userRegister(name, email, password)
}