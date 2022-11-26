package com.ferdifir.slowgram.presentation.ui.auth

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.ferdifir.slowgram.R
import com.ferdifir.slowgram.databinding.ActivityAuthBinding
import java.util.*

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fragmentSetup()

        val handler = Handler(Looper.getMainLooper())
        val random = Random()
        val timeDuration = (1000..6000).random().toLong()
        val task: Runnable = object : Runnable {
            override fun run() {
                val widthDp = resources.displayMetrics.run { widthPixels / density }
                val translationX = random.nextInt(widthDp.toInt()).toFloat()
                ObjectAnimator.ofFloat(binding.logo, View.TRANSLATION_X, translationX).apply {
                    duration = timeDuration
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                }.start()
                val translationY = random.nextInt(widthDp.toInt()).toFloat()
                ObjectAnimator.ofFloat(binding.logo, View.TRANSLATION_Y, translationY).apply {
                    duration = timeDuration
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                }.start()
                handler.postDelayed(this, 3000)
            }
        }
        task.run()
    }

    private fun fragmentSetup() {
        supportFragmentManager.beginTransaction()
            .add(R.id.auth_container, LoginFragment())
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}