package com.dicoding.picodiploma.loginwithanimation.view.login

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.api.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    private val _user = MutableLiveData<LoginResponse>()
    val user: LiveData<LoginResponse> = _user



    fun login(binding: ActivityLoginBinding,email: String, password: String): LiveData<LoginResponse> {
        _isLoading.value = true

        val client = ApiConfig.getApiService().loginUser(email, password);


        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if(response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody!= null && !responseBody.error){
                        viewModelScope.launch {
                            pref.login(responseBody.loginResult)


                        }
                        _user.value = response.body()

                    }else{
                        Log.d("test", "Masuk Sini Else")
                        _user.value = response.body()
                        binding.emailEditTextLayout.error= "Email Tidak Sesuai"
                        binding.passwordEditText.error= "Password Tidak Sesuai"

                    }
                }else{
                    if(response.message()=="Unauthorized"){
                        binding.passwordEditTextLayout.error= "Password Tidak Sesuai"
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                TODO("Not yet implemented")
                Log.d("test", "Masuk Sini")
            }
            
        })
        return _user
    }
}