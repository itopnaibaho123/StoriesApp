package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.picodiploma.loginwithanimation.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.api.DetailStoryResponse
import com.dicoding.picodiploma.loginwithanimation.api.Story
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(private val pref: UserPreference): ViewModel() {
    private val _detail = MutableLiveData<Story>()
    val detail: LiveData<Story> = _detail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object{
        internal const val TAG = "DetailViewModel"
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }
    fun getDetailStory(token: String, id: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailStories(token,id)
        client.enqueue(object : Callback<DetailStoryResponse> {
            override fun onResponse(
                call: Call<DetailStoryResponse>,
                response: Response<DetailStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _detail.value = response.body()?.story;
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

}