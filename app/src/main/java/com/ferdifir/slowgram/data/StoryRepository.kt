package com.ferdifir.slowgram.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.ferdifir.slowgram.data.datastore.UserPreferences
import com.ferdifir.slowgram.data.local.entity.StoryEntity
import com.ferdifir.slowgram.data.local.room.StoryDatabase
import com.ferdifir.slowgram.data.remote.StoryRemoteMediator
import com.ferdifir.slowgram.data.remote.response.LoginResponse
import com.ferdifir.slowgram.data.remote.response.RegisterResponse
import com.ferdifir.slowgram.data.remote.response.StoriesResponse
import com.ferdifir.slowgram.data.remote.response.UploadResponse
import com.ferdifir.slowgram.data.remote.retrofit.ApiService
import com.ferdifir.slowgram.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val pref: UserPreferences,
    private val database: StoryDatabase,
    private val apiService: ApiService
) {

    fun userLogin(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        try {
            val response = apiService.userLogin(email, password)
            val token = response.loginResult.token
            pref.saveLoginSession(token)
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.toString()))
        }

    }

    fun userRegister(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        try {
            val response = apiService.userRegister(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.toString()))
        }
    }

    fun getStoriesWithLocation(token: String): LiveData<Result<StoriesResponse>> = liveData {
        try {
            val getToken = generateBearerToken(token)
            val response = apiService.getAllStories(token = getToken, location = 1)
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.toString()))
        }
    }

    fun uploadStory(
        token: String,
        description: RequestBody,
        image: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): LiveData<Result<UploadResponse>> = liveData {
        val getToken = generateBearerToken(token)
        try {
            val response = apiService.uploadStory(
                getToken, image, description, lat, lon
            )
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("UPLOAD", e.toString())
            emit(Result.Error(e.toString()))
        }
    }

    fun getLoginSession(): LiveData<String?> {
        return pref.getUserToken().asLiveData()
    }

    suspend fun clearLoginSession() {
        return pref.clearLoginSession()
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(token: String): LiveData<PagingData<StoryEntity>> {
        val getToken = generateBearerToken(token)
        return Pager(
            config = PagingConfig(
                10
            ),
            remoteMediator = StoryRemoteMediator(
                database,
                apiService,
                getToken
            ),
            pagingSourceFactory = {
                database.storyDao().getStories()
            }
        ).liveData
    }

    private fun generateBearerToken(token: String): String {
        return "Bearer $token"
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            pref: UserPreferences,
            database: StoryDatabase,
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(pref, database, apiService)
            }.also { instance = it }
    }
}