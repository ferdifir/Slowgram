package com.ferdifir.slowgram.presentation.ui.upload

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ferdifir.slowgram.data.StoryRepository
import com.ferdifir.slowgram.data.remote.response.UploadResponse
import com.ferdifir.slowgram.utils.Result
import com.ferdifir.slowgram.utils.DataDummy
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
class UploadViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var viewModel: UploadViewModel
    private val dummyToken = "auth_token"
    private val dummyUploadResponse = DataDummy.generateDummyFileUploadResponse()
    private val dummyMultipart = DataDummy.generateDummyMultipartFile()
    private val dummyDescription = DataDummy.generateDummyRequestBody()

    @Before
    fun setup() {
        viewModel = UploadViewModel(repository)
    }

    @Test
    fun `Get authentication token`() {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = dummyToken

        `when`(repository.getLoginSession()).thenReturn(expectedToken)

        val actualToken = viewModel.getUserToken().getOrAwaitValue()
        Mockito.verify(repository).getLoginSession()
        assertNotNull(actualToken)
        assertEquals(dummyToken, actualToken)
    }

    @Test
    fun `Upload Story successfully`() {
        val expectedResponse = MutableLiveData<Result<UploadResponse>>()
        expectedResponse.value = Result.Success(dummyUploadResponse)

        `when`(repository.uploadStory(dummyToken, dummyDescription, dummyMultipart)).thenReturn(expectedResponse)

        val actualResponse = viewModel.uploadStory(dummyToken, dummyMultipart, dummyDescription).getOrAwaitValue()
        Mockito.verify(repository).uploadStory(dummyToken, dummyDescription, dummyMultipart)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `Upload Story failed`() {
        val expectedResponse = MutableLiveData<Result<UploadResponse>>()
        expectedResponse.value = Result.Error("Error")

        `when`(repository.uploadStory(dummyToken, dummyDescription, dummyMultipart)).thenReturn(expectedResponse)

        val actualResponse = viewModel.uploadStory(dummyToken, dummyMultipart, dummyDescription).getOrAwaitValue()
        Mockito.verify(repository).uploadStory(dummyToken, dummyDescription, dummyMultipart)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Error)
    }
}