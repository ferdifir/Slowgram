package com.ferdifir.slowgram.presentation.ui.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ferdifir.slowgram.data.StoryRepository
import com.ferdifir.slowgram.data.remote.response.StoriesResponse
import com.google.android.gms.maps.model.LatLng
import com.ferdifir.slowgram.utils.Result

class MapsViewModel(private val repository: StoryRepository): ViewModel() {
    val coordinateTemp = MutableLiveData(LatLng(-2.3932797, 108.8507139))
    fun getStoriesWithLocation(token: String): LiveData<Result<StoriesResponse>> {
        return repository.getStoriesWithLocation(token)
    }
}
