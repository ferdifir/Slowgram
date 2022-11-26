package com.ferdifir.slowgram.presentation.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ferdifir.slowgram.data.local.entity.StoryEntity
import com.ferdifir.slowgram.databinding.ItemListSavedBinding
import com.ferdifir.slowgram.presentation.ui.detail.DetailActivity
import com.ferdifir.slowgram.utils.Const.EXTRA_DETAIL

class SavedAdapter: RecyclerView.Adapter<SavedAdapter.SavedViewHolder>() {

    private var listData = ArrayList<StoryEntity>()

    inner class SavedViewHolder(private val binding: ItemListSavedBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoryEntity) {
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(binding.ivListSaved)
            binding.root.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        binding.root.context as Activity,
                        Pair(binding.ivListSaved, "image")
                    )
                Intent(itemView.context, DetailActivity::class.java).also { intent ->
                    intent.putExtra(EXTRA_DETAIL, data)
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    fun setData(newListData: List<StoryEntity>?) {
        if (newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder =
        SavedViewHolder(ItemListSavedBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

}