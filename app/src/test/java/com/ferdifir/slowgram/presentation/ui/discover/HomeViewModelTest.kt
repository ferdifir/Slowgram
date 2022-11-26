package com.ferdifir.slowgram.presentation.ui.discover

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.ferdifir.slowgram.data.StoryRepository
import com.ferdifir.slowgram.data.local.entity.StoryEntity
import com.ferdifir.slowgram.presentation.adapter.StoryAdapter
import com.ferdifir.slowgram.utils.DataDummy
import com.ferdifir.slowgram.utils.MainDispatcherRule
import com.ferdifir.slowgram.utils.PagedTestDataSource
import com.ferdifir.slowgram.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var homeViewModel: HomeViewModel
    private val dummyToken = "token123"

    @Before
    fun setup() {
        homeViewModel = HomeViewModel(storyRepository)
    }

    @Test
    fun `when Get Story Should Not Null and Return Success`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryEntity()
        val data: PagingData<StoryEntity> = PagedTestDataSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data

        `when`(storyRepository.getStories(dummyToken)).thenReturn(expectedStory)

        val actualStory = homeViewModel.getStories(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory, differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)
    }

    @Test
    fun `User logout should call userLogout in StoryRepository`() = runTest {
        homeViewModel.userLogout()
        Mockito.verify(storyRepository).clearLoginSession()
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

}