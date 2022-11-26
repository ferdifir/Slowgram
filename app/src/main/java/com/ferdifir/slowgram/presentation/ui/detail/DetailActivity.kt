package com.ferdifir.slowgram.presentation.ui.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.ferdifir.slowgram.R
import com.ferdifir.slowgram.data.local.entity.StoryEntity
import com.ferdifir.slowgram.databinding.ActivityDetailBinding
import com.ferdifir.slowgram.utils.Const.EXTRA_DETAIL
import com.ferdifir.slowgram.utils.Helper
import com.ferdifir.slowgram.utils.Helper.setLocalDateFormat

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val story = intent.getParcelableExtra<StoryEntity>(EXTRA_DETAIL)
        showDetailStory(story)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun showDetailStory(story: StoryEntity?) {
        if (story != null) {
            binding.apply {
                tvListUsername.text = story.name
                if (story.lat != null && story.lon != null) {
                    tvListLocation.visibility = View.VISIBLE
                    tvListLocation.text = Helper.parseAddressLocation(this@DetailActivity, story.lat, story.lon)
                } else {
                    tvListLocation.visibility = View.GONE
                }
                tvDetailDesc.text = story.description
                val loading = CircularProgressDrawable(this@DetailActivity)
                loading.setColorSchemeColors(
                    ContextCompat.getColor(this@DetailActivity, R.color.teal_200),
                    ContextCompat.getColor(this@DetailActivity, R.color.teal_700),
                    ContextCompat.getColor(this@DetailActivity, R.color.gray)
                )
                loading.centerRadius = 30f
                loading.strokeWidth = 5f
                loading.start()
                tvCreated.setLocalDateFormat(story.createdAt)
                Glide.with(this@DetailActivity)
                    .load(story.photoUrl)
                    .placeholder(loading)
                    .into(ivListContent)

            }
        }
    }
}