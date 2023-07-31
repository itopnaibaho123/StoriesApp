package com.dicoding.picodiploma.loginwithanimation.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.dicoding.myunlimitedquotes.data.StoryPagingResource
import com.dicoding.picodiploma.loginwithanimation.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.api.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.api.StoriesResponse

class StoryRepository(private val apiService: ApiService) {
    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),

            pagingSourceFactory = {
                StoryPagingResource(token,apiService)

            }
        ).liveData
    }
    fun getStoriesLocationOnly(token: String): LiveData<StoriesResponse> =
        liveData {

            try {
                val response = apiService.getAllStories(token)
                if (response.error) {
                    emit(response)
                } else {
                    emit(response)
                }
            } catch (e: Exception) {
                emit(StoriesResponse(error= true, message = "Failed"))
            }
        }
}