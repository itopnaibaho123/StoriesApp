package com.dicoding.picodiploma.loginwithanimation.view.main

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.api.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.api.StoriesResponse
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.model.repository.StoryRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreference, private val storyRepository: StoryRepository) : ViewModel() {
//
//    private val _isLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> = _isLoading

//    private val _listItem = LiveData<PagingData<ListStoryItem>>()
//    val listItem: LiveData<PagingData<ListStoryItem>> = _listItem


    companion object{
        private const val TAG = "MainViewModel"
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }
    fun getAllPost(token: String):  LiveData<PagingData<ListStoryItem>> {
//        Log.d("Test", "Masukk ke getAllPost")
        return storyRepository.getStories(token)
    }



//    fun getAllPost(token: String){
//        _isLoading.value = true

//        val client = ApiConfig.getApiService().getAllStories(token)
//        client.enqueue(object : Callback<StoriesResponse> {
//            override fun onResponse(
//                call: Call<StoriesResponse>,
//                response: Response<StoriesResponse>
//            ) {
//                _isLoading.value = false
//                if (response.isSuccessful) {
//                    _listItem.value = response.body()?.listStory as List<ListStoryItem>?
//
//                } else {
//                    Log.e(TAG, "onFailure: ${response.message()}")
//                }
//            }
//            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
//                _isLoading.value = false
//                Log.e(TAG, "onFailure: ${t.message.toString()}")
//            }
//        })
//    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}