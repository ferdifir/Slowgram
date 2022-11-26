package com.ferdifir.slowgram.presentation.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ferdifir.slowgram.data.StoryRepository
import com.ferdifir.slowgram.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.ferdifir.slowgram.utils.Result

class UploadViewModel(private val repository: StoryRepository): ViewModel() {
    fun getUserToken(): LiveData<String?> {
        return repository.getLoginSession()
    }
    fun uploadStory(
        token: String,
        image: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null,
    ): LiveData<Result<UploadResponse>> {
        return repository.uploadStory(token, description, image, lat, lon)
    }
}