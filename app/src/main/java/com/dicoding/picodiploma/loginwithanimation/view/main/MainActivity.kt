package com.dicoding.picodiploma.loginwithanimation.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.api.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.model.StoryModel
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.model.repository.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.view.Maps.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.adapter.LoadingListAdapter
import com.dicoding.picodiploma.loginwithanimation.view.adapter.StoryAdapter
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.post.AddPostActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private  lateinit var adapter:StoryAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref, StoryRepository(ApiConfig.getApiService()))).get(MainViewModel::class.java)

        val layoutManager = LinearLayoutManager(this)
        binding.rvPost.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvPost.addItemDecoration(itemDecoration)

//        mainViewModel.getToken().observe(this) {
//            mainViewModel.getAllPost("Bearer $it")
//        }

//        mainViewModel.listItem.observe(this) { items ->
//            setPostData(items)
//        }

//        mainViewModel.isLoading.observe(this) {
//            showLoading(it, binding.progressBar)
//        }
        setupView()
        setupViewModel()
        setupData()


//        playAnimation()
    }

    override fun onResume() {
        super.onResume()

        setupData()
    }

    private fun setupData() {


        adapter = StoryAdapter()
        binding.rvPost.adapter = adapter.withLoadStateFooter(
            footer = LoadingListAdapter {
                adapter.retry()
            }
        )
        mainViewModel.getToken().observe(this){
            Log.d("Token", it.toString())
            mainViewModel.getAllPost("Bearer $it").observe(this,{

                adapter.submitData(lifecycle,it)
            })
        }


        adapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: StoryModel) {
                showSelectedUser(data)
            }
        })
    }
    private fun showSelectedUser(data: StoryModel) {
        val moveWithParcelableIntent = Intent(this@MainActivity, DetailStoryActivity::class.java)
        moveWithParcelableIntent.putExtra(DetailStoryActivity.ID, data.id)
        startActivity(moveWithParcelableIntent)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
//        supportActionBar?.hide()
    }


    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), StoryRepository(ApiConfig.getApiService())))[MainViewModel::class.java]

        mainViewModel.getUser().observe(this, { user ->
            if (user.token != ""){
//                binding.nameTextView.text = getString(R.string.greeting, user.name)
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        })
    }


    fun showLoading(isLoading: Boolean, view: View) {
        if (isLoading) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.INVISIBLE
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                mainViewModel.logout()
                return true
            }
            R.id.maps -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.addPost -> {
                val intent = Intent(this@MainActivity, AddPostActivity::class.java)
                startActivity(intent)
                true
            }
            else -> true
        }
    }

}