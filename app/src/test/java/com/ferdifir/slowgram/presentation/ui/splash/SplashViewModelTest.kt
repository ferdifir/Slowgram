package com.ferdifir.slowgram.presentation.ui.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ferdifir.slowgram.data.StoryRepository
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
class SplashViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var viewModel: SplashViewModel
    private val dummyToken = "auth_token"

    @Before
    fun setup() {
        viewModel = SplashViewModel(repository)
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
}