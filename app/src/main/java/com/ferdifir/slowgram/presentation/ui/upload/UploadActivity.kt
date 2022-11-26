package com.ferdifir.slowgram.presentation.ui.upload

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ferdifir.slowgram.R
import com.ferdifir.slowgram.databinding.ActivityUploadBinding
import com.ferdifir.slowgram.presentation.viewmodel.ViewModelFactory
import com.ferdifir.slowgram.utils.Helper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: UploadViewModel
    private var location: Location? = null
    private lateinit var token: String
    private var positionLat: RequestBody? = null
    private var positionLon: RequestBody? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[UploadViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLocation()

        setSupportActionBar(binding.toolbarUpload)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val myFile = intent?.getSerializableExtra(EXTRA_PHOTO_RESULT) as File
        val isBackCamera = intent?.getBooleanExtra(EXTRA_CAMERA_MODE, true) as Boolean
        val rotatedBitmap = Helper.rotateBitmap(
            BitmapFactory.decodeFile(myFile.path),
            isBackCamera
        )

        binding.toolbarUpload.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.ivPreview.setImageBitmap(rotatedBitmap)

        binding.switchAddLocation.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                binding.switchAddLocation.text = location?.let {
                    Helper.parseAddressLocation(
                        this,
                        it.latitude,
                        location!!.longitude
                    )
                }
                positionLat = location?.latitude?.toString()?.toRequestBody("text/plain".toMediaType())!!
                positionLon = location?.longitude?.toString()?.toRequestBody("text/plain".toMediaType())!!
            } else {
                binding.switchAddLocation.text = getString(R.string.const_text_switch)
            }
        }

        val uploadImage = Helper.reduceFileSize(myFile)
        val requestImage = uploadImage.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val reqImageFile = MultipartBody.Part.createFormData("photo", myFile.name, requestImage)

        viewModel.getUserToken().observe(this) {
            if (it != null) {
                token = it
            }
        }

        binding.btnUpload.setOnClickListener {
            binding.progressCircular.visibility = View.VISIBLE
            uploadStory(token, reqImageFile, positionLat, positionLon)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            } else {
                Helper.notifyGivePermission(
                    this,
                    "Give the app access to read your location"
                )
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    this.location = location
                } else {
                    Toast.makeText(this, getString(R.string.toast_activate_location), Toast.LENGTH_LONG).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun uploadStory(
        token: String,
        image: MultipartBody.Part,
        latitude: RequestBody? = null,
        longitude: RequestBody? = null
    ) {
        val desc = binding.tvUploadDescription.text.toString()
        val description = desc.toRequestBody("text/plain".toMediaType())

        if (desc.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_caption_empty), Toast.LENGTH_LONG).show()
        } else {
            viewModel.uploadStory(token, image, description, latitude, longitude)
                .observe(this) { result ->
                    if (result != null) {
                        when(result) {
                            is com.ferdifir.slowgram.utils.Result.Success -> {
                                binding.progressCircular.visibility = View.GONE
                                Toast.makeText(this, "Story uploaded", Toast.LENGTH_LONG).show()
                                setResult(RESULT_OK)
                                finish()
                            }
                            is com.ferdifir.slowgram.utils.Result.Error -> {
                                binding.progressCircular.visibility = View.GONE
                                Toast.makeText(
                                    this,
                                    "Something went wrong, try again",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            is com.ferdifir.slowgram.utils.Result.Loading -> {

                            }
                        }
                    }
                }
        }
    }

    companion object {
        const val EXTRA_PHOTO_RESULT = "PHOTO_RESULT"
        const val EXTRA_CAMERA_MODE = "CAMERA_MODE"
    }
}