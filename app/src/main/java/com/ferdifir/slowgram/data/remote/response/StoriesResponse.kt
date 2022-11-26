package com.ferdifir.slowgram.data.remote.response

import com.google.gson.annotations.SerializedName

data class StoriesResponse(

    @field:SerializedName("listStory")
	val listStory: List<ListStoryItem>,

    @field:SerializedName("error")
	val error: Boolean,

    @field:SerializedName("message")
	val message: String
)