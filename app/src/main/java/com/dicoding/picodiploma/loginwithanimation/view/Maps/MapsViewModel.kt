package com.dicoding.picodiploma.loginwithanimation.view.Maps

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.api.Response.PostResponse
import com.dicoding.picodiploma.loginwithanimation.api.StoriesResponse
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.model.repository.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.view.signup.SignupViewModel
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(
    private val pref: UserPreference,
    private val storyRepository: StoryRepository,

) : ViewModel() {
    fun getStories(token: String) = storyRepository.getStoriesLocationOnly(token)

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

}