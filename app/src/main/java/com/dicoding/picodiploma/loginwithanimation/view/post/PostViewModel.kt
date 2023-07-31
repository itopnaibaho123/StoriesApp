package com.dicoding.picodiploma.loginwithanimation.view.post

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.api.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.api.StoriesResponse
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listItem = MutableLiveData<List<ListStoryItem>>()
    val listItem: LiveData<List<ListStoryItem>> = _listItem


    companion object{
        private const val TAG = "MainViewModel"
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }
}