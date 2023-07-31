package com.dicoding.picodiploma.loginwithanimation.view.post

import android.Manifest
import android.R.attr.bitmap
import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.api.Response.PostResponse
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddPostBinding
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddPostActivity : AppCompatActivity() {
    private lateinit var postViewModel: PostViewModel
    private lateinit var binding: ActivityAddPostBinding
    private lateinit var currentPhotoPath: String

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var location: Location? = null


    private var getFile: File? = null

    companion object {


        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkCoarseLocationFineLocationPermission()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        val pref = UserPreference.getInstance(dataStore)
        postViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(PostViewModel::class.java)


        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }


    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddPostActivity,
                "com.dicoding.picodiploma.loginwithanimation",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            intent.putExtra("android.intent.extras.CAMERA_FACING", false)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    checkCoarseLocationFineLocationPermission()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    checkCoarseLocationFineLocationPermission()
                }
                else -> {
                    // No location access granted.
                }
            }
        }


    private fun checkCoarseLocationFineLocationPermission() {
        val coarseLocation = checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        val fineLocation = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (coarseLocation && fineLocation) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    this.location = location
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun uploadImage() {
        postViewModel.getToken().observe(this@AddPostActivity) {
            if (getFile != null) {
                Log.d("Token", it)
                val file = reduceFileImage(getFile as File)

                val description = binding.descriptionEditText.text.toString()
                    .toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                Log.d("Description", binding.descriptionEditText.text.toString())
                val textPlainMediaType = "text/plain".toMediaType()

                var service = ApiConfig.getApiService().addStory(token = "Bearer $it",
                    file = imageMultipart,
                    description = description,
                    lon = location?.longitude.toString().toRequestBody(textPlainMediaType),
                    lat = location?.latitude.toString().toRequestBody(textPlainMediaType));

                service.enqueue(object : Callback<PostResponse> {
                    override fun onResponse(
                        call: Call<PostResponse>,
                        response: Response<PostResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error) {
                                Toast.makeText(this@AddPostActivity,
                                    responseBody.message,
                                    Toast.LENGTH_SHORT).show()
                                finish()

                            }
                        } else {
                            Toast.makeText(this@AddPostActivity,
                                response.message(),
                                Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                        Toast.makeText(this@AddPostActivity,
                            "Gagal instance Retrofit",
                            Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Log.d("test", "GANTENG")
                Toast.makeText(this@AddPostActivity,
                    "Silakan masukkan berkas gambar terlebih dahulu.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }


    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val cameraInfo = Camera.CameraInfo()
        val cameraId = intent.extras?.getInt("android.intent.extras.CAMERA_FACING")
            ?: Camera.CameraInfo.CAMERA_FACING_BACK
        Camera.getCameraInfo(cameraId, cameraInfo)
        val isBackCamera = cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                rotateFile(file, isBackCamera)
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
            getFile = myFile
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@AddPostActivity)

            getFile = myFile

            binding.previewImageView.setImageURI(selectedImg)
        }
    }
}