package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.picodiploma.loginwithanimation.MainDispatcherRule
import com.dicoding.picodiploma.loginwithanimation.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.api.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.model.repository.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.utils.DataDummy
import com.dicoding.picodiploma.loginwithanimation.utils.getOrAwaitValue
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.adapter.StoryAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")



@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)


class MainViewModelTest {


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var  pref: UserPreference


    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `when GET stories return list`() =  mainDispatcherRule.runBlockingTest {
        val token = "token"
        val dummyData = DataDummy.generateDummyResponse()
        val data = PagingData.from(dummyData)
        val listStory = MutableLiveData<PagingData<ListStoryItem>>()
        listStory.value = data

        `when` (storyRepository.getStories(token)).thenReturn(listStory)
        val mainViewModel =  MainViewModel(pref, storyRepository)


        val result =mainViewModel.getAllPost(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainDispatcherRule.dispatcher,
            workerDispatcher = mainDispatcherRule.dispatcher
        )
        differ.submitData(result)
        advanceUntilIdle()

        verify(storyRepository).getStories(token)
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyData.size, differ.snapshot().size)
        Assert.assertEquals(dummyData[0], differ.snapshot()[0])

    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `when GET stories return null`() =  mainDispatcherRule.runBlockingTest {
        val token = "token"
        val dummyData = DataDummy.generateNullDummyData()
        val data = PagingData.from(dummyData)
        val listStory = MutableLiveData<PagingData<ListStoryItem>>()
        listStory.value = data

        `when` (storyRepository.getStories(token)).thenReturn(listStory)
        val mainViewModel =  MainViewModel(pref, storyRepository)


        val result =mainViewModel.getAllPost(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainDispatcherRule.dispatcher,
            workerDispatcher = mainDispatcherRule.dispatcher
        )
        differ.submitData(result)
        advanceUntilIdle()

        verify(storyRepository).getStories(token)
        Assert.assertEquals(0, differ.snapshot().size)
        Assert.assertEquals(0, differ.snapshot().size)

    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}