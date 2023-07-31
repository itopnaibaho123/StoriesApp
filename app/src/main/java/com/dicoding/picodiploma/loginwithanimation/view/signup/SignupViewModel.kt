package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.api.Response.PostResponse
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _user = MutableLiveData<PostResponse>()
    val user : LiveData<PostResponse> = _user

    companion object{
        private val TAG = SignupViewModel::class.java.simpleName
    }


    fun saveUser(name: String, email:String, password: String): LiveData<PostResponse> {

        _isLoading.value = true
        val client = ApiConfig.getApiService().createUser(name, email, password);
        client.enqueue(object : Callback<PostResponse> {
            override fun onResponse(
                call: Call<PostResponse>,
                response: Response<PostResponse>,
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _user.value = response.body()
                        Log.e(TAG, "Failed: Response Unsuccessful- ${response.message()}")
                    } else {
                        _user.value = response.body()
                        Log.e(TAG, "Failed: Response Unsuccessful- ${response.message()}")
                    }
                }
                if(response.code() == 400) {
                    val gson = Gson()
                    val message: PostResponse = gson.fromJson(response.errorBody()!!.charStream(),
                        PostResponse::class.java)
                    _user.value = message
                }

            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {

                Log.e(TAG, "Failed: Response Failure- ${t.message.toString()}")
            }

        })
        return _user
    }
}