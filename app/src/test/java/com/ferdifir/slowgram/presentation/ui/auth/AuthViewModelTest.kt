package com.ferdifir.slowgram.presentation.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ferdifir.slowgram.data.StoryRepository
import com.ferdifir.slowgram.data.remote.response.LoginResponse
import com.ferdifir.slowgram.data.remote.response.RegisterResponse
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
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var authViewModel: AuthViewModel
    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()
    private val dummyName = "fred"
    private val dummyEmail = "fred@domain.com"
    private val dummyPassword = "passwd"

    @Before
    fun setup() {
        authViewModel = AuthViewModel(storyRepository)
    }

    @Test
    fun `Login successfully - result success`() {
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.Success(dummyLoginResponse)

        `when`(storyRepository.userLogin(dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        val actualResponse = authViewModel.userLogin(dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(storyRepository).userLogin(dummyEmail, dummyPassword)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `Login failed - result error`() {
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.Error("Error")

        `when`(storyRepository.userLogin(dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        val actualResponse = authViewModel.userLogin(dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(storyRepository).userLogin(dummyEmail, dummyPassword)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Error)
    }

    @Test
    fun `Registration successfully - result success`() {
        val expectedResponse = MutableLiveData<Result<RegisterResponse>>()
        expectedResponse.value = Result.Success(dummyRegisterResponse)

        `when`(storyRepository.userRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        val actualResponse = authViewModel.userRegister(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(storyRepository).userRegister(dummyName, dummyEmail, dummyPassword)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `Registration failed - result error`() {
        val expectedResponse = MutableLiveData<Result<RegisterResponse>>()
        expectedResponse.value = Result.Error("Error")

        `when`(storyRepository.userRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        val actualResponse = authViewModel.userRegister(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(storyRepository).userRegister(dummyName, dummyEmail, dummyPassword)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Error)
    }

}