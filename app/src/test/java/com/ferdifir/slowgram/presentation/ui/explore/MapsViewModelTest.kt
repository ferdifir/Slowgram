package com.ferdifir.slowgram.presentation.ui.explore

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ferdifir.slowgram.data.StoryRepository
import com.ferdifir.slowgram.data.remote.response.StoriesResponse
import com.ferdifir.slowgram.utils.DataDummy
import com.ferdifir.slowgram.utils.Result
import com.ferdifir.slowgram.utils.getOrAwaitValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var viewModel: MapsViewModel
    private val dummyToken = "auth_token"
    private val dummyStoriesResponse = DataDummy.generateDummyStoriesResponse()

    @Before
    fun setup() {
        viewModel = MapsViewModel(repository)
    }

    @Test
    fun `Get stories with location - result success`() {
        val expectedStories = MutableLiveData<Result<StoriesResponse>>()
        expectedStories.value = Result.Success(dummyStoriesResponse)

        `when`(repository.getStoriesWithLocation(dummyToken)).thenReturn(expectedStories)

        val actualStories = viewModel.getStoriesWithLocation(dummyToken).getOrAwaitValue()
        Mockito.verify(repository).getStoriesWithLocation(dummyToken)
        assertNotNull(actualStories)
        assertTrue(actualStories is Result.Success)
    }

    @Test
    fun `Get stories with location - result error`() {
        val expectedStories = MutableLiveData<Result<StoriesResponse>>()
        expectedStories.value = Result.Error("Error")

        `when`(repository.getStoriesWithLocation(dummyToken)).thenReturn(expectedStories)

        val actualStories = viewModel.getStoriesWithLocation(dummyToken).getOrAwaitValue()
        Mockito.verify(repository).getStoriesWithLocation(dummyToken)
        assertNotNull(actualStories)
        assertTrue(actualStories is Result.Error)
    }
}