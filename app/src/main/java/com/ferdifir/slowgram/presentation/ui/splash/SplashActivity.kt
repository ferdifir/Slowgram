package com.ferdifir.slowgram.presentation.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ferdifir.slowgram.presentation.ui.auth.AuthActivity
import com.ferdifir.slowgram.presentation.ui.main.MainActivity
import com.ferdifir.slowgram.presentation.viewmodel.ViewModelFactory
import com.ferdifir.slowgram.utils.Const
import com.ferdifir.slowgram.utils.Const.EXTRA_TOKEN

@SuppressLint("CustomSplashScreen")
class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ViewModelFactory.getInstance(this)
        val viewModel: SplashViewModel by viewModels { factory }
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.getUserToken().observe(this) { token ->
                if (token.isNullOrEmpty()) {
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(EXTRA_TOKEN, token)
                    startActivity(intent)
                    finish()
                }
            }
        }, 15000)
    }
}