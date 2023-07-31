package com.dicoding.picodiploma.loginwithanimation.api


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.picodiploma.loginwithanimation.api.Response.PostResponse
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("v1/stories/guest")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<PostResponse>

    @Multipart
    @POST("v1/stories")
    fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part("lat") lat: RequestBody
    ): Call<PostResponse>

    @FormUrlEncoded
    @POST("v1/register")
    fun createUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<PostResponse>

    @FormUrlEncoded
    @POST("v1/login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    //    @Headers("Authorization: token <Personal Access Token>")
    @GET("v1/stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 1
    ): StoriesResponse

    @GET("v1/stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("location") location: Int? = 1
    ): StoriesResponse


    //    @Headers("Authorization: token <Personal Access Token>")
    @GET("v1/stories/{id}")
    fun getDetailStories(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<DetailStoryResponse>
}