package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.api.Story
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailStoryBinding
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "setting")

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var detailViewModel: DetailViewModel
    private lateinit var binding: ActivityDetailStoryBinding
    companion object {
        const val ID = "extra_id"

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val pref = UserPreference.getInstance(dataStore)

        val detailViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(
            DetailViewModel::class.java
        )
        val id = intent.getStringExtra(ID) as String;

        detailViewModel.getToken().observe(this) {
            detailViewModel.getDetailStory("Bearer $it", id)
        }

        detailViewModel.detail.observe(this) { detail ->
            setDataToView(detail)
        }

        detailViewModel.isLoading.observe(this) {
            showLoading(it, binding.progressBar)
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setDataToView(detailList: Story) {
        binding.apply {
            Glide.with(this@DetailStoryActivity)
                .load(detailList.photoUrl)
                .circleCrop()
                .into(imageView)
            nama.text = detailList.name
            description.text = detailList.description ?: "No name."
        }
    }
    fun showLoading(isLoading: Boolean, view: View) {
        if (isLoading) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.INVISIBLE
        }
    }

}