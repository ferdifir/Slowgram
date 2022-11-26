package com.ferdifir.slowgram.presentation.ui.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.ferdifir.slowgram.R
import com.ferdifir.slowgram.databinding.ActivityMainBinding
import com.ferdifir.slowgram.presentation.ui.discover.HomeFragment
import com.ferdifir.slowgram.presentation.ui.explore.MapsFragment
import com.ferdifir.slowgram.presentation.ui.upload.CameraActivity
import com.ferdifir.slowgram.utils.Helper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var startNewStory: ActivityResultLauncher<Intent>
    private lateinit var fragmentHome: HomeFragment

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fragmentHome = HomeFragment()
        val fragmentExplore = MapsFragment()

        startNewStory =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    fragmentHome.onRefresh()
                }
            }

        binding.bottomNavigationView.background = null

        switchFragment(fragmentHome)
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> {
                    switchFragment(fragmentHome)
                    true
                }
                R.id.nav_explore -> {
                    if (Helper.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    ) {
                        switchFragment(fragmentExplore)
                        true
                    } else {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            LOCATION_PERMISSION_CODE
                        )
                        false
                    }
                }
                else -> false
            }
        }
        binding.fab.setOnClickListener {
            if (Helper.isPermissionGranted(this, Manifest.permission.CAMERA)) {
                val intent = Intent(this@MainActivity, CameraActivity::class.java)
                startNewStory.launch(intent)
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    companion object {
        const val LOCATION_PERMISSION_CODE = 30
        const val CAMERA_PERMISSION_CODE = 10
    }
}