package com.ferdifir.slowgram.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.ferdifir.slowgram.data.datastore.UserPreferences
import com.ferdifir.slowgram.data.local.entity.StoryEntity
import com.ferdifir.slowgram.data.local.room.StoryDatabase
import com.ferdifir.slowgram.data.remote.retrofit.ApiService
import com.ferdifir.slowgram.presentation.adapter.StoryAdapter
import com.ferdifir.slowgram.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock private lateinit var preferences: UserPreferences

    @Mock private lateinit var database: StoryDatabase

    @Mock private lateinit var apiService: ApiService

    @Mock private lateinit var repositoryMock: StoryRepository

    private lateinit var storyRepository: StoryRepository

    @Before
    fun setup() {
        storyRepository = StoryRepository(preferences, database, apiService)
    }

    private val dummyName = "fred"
    private val dummyEmail = "fred@domain.com"
    private val dummyPassword = "passwd"
    private val dummyToken = "authentication_token"
    private val dummyMultipart = DataDummy.generateDummyMultipartFile()
    private val dummyDescription = DataDummy.generateDummyRequestBody()
    private val dummyStoriesResponse = DataDummy.generateDummyStoriesResponse()

    @Test
    fun `user login successfully`() = runTest {
        val expectedResponse = DataDummy.generateDummyLoginResponse()
        `when`(apiService.userLogin(dummyEmail, dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = storyRepository.userLogin(dummyEmail, dummyPassword).getOrAwaitValue()
        assertNotNull(actualResponse)
    }

    @Test
    fun `user register successfully`() = runTest {
        val expectedResponse = DataDummy.generateDummyRegisterResponse()
        `when`(apiService.userRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = storyRepository.userRegister(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        assertNotNull(actualResponse)
    }

    @Test
    fun `get stories with location successfully`() = runTest {
        val expectedResult = DataDummy.generateDummyStoriesResponse()
        lenient(). `when`(apiService.getAllStories(dummyToken)).thenReturn(expectedResult)
        val actualResult = storyRepository.getStoriesWithLocation(dummyToken).getOrAwaitValue()
        assertNotNull(actualResult)
    }

    @Test
    fun `upload story successfully`() = runTest {
        val expectedResponse = DataDummy.generateDummyFileUploadResponse()
        lenient().`when`(apiService.uploadStory(
            dummyToken,
            dummyMultipart,
            dummyDescription
        )).thenReturn(expectedResponse)
        val actualResponse = storyRepository.uploadStory(
            dummyToken,
            dummyDescription,
            dummyMultipart
        ).getOrAwaitValue()
        assertNotNull(actualResponse)
    }

    @Test
    fun `get user token successfully`() = runTest {
        val expectedToken = flowOf(dummyToken)
        `when`(preferences.getUserToken()).thenReturn(expectedToken)
        val actualToken = storyRepository.getLoginSession().getOrAwaitValue()
        assertNotNull(actualToken)
        assertEquals(dummyToken, actualToken)
    }

    @Test
    fun `user logout`() = runTest {
        storyRepository.clearLoginSession()
        Mockito.verify(preferences).clearLoginSession()
    }

    @Test
    fun `get stories with pager successfully`() = runTest {
        val dummyStories = DataDummy.generateDummyStoryEntity()
        val data = PagedTestDataSource.snapshot(dummyStories)

        val expectedResult = MutableLiveData<PagingData<StoryEntity>>()
        expectedResult.value = data

        `when`(repositoryMock.getStories(dummyToken)).thenReturn(expectedResult)

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainDispatcherRule.testDispatcher,
            workerDispatcher = mainDispatcherRule.testDispatcher
        )
        val actualResult = repositoryMock.getStories(dummyToken).getOrAwaitValue()
        differ.submitData(actualResult)

        assertNotNull(differ.snapshot())
        assertEquals(
            dummyStoriesResponse.listStory.size,
            differ.snapshot().size
        )
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}