package com.ferdifir.slowgram.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "story")
data class StoryEntity(
	@ColumnInfo(name = "photo_url")
	val photoUrl: String,

	@ColumnInfo(name = "date")
	val createdAt: String,

	@ColumnInfo(name = "name")
	val name: String,

	@ColumnInfo(name = "description")
	val description: String,

	@ColumnInfo(name = "longitude")
	val lon: Double? = null,

	@PrimaryKey
	@ColumnInfo(name = "id")
	val id: String,

	@ColumnInfo(name = "latitude")
	val lat: Double? = null,

	@ColumnInfo(name = "isSaved")
	var isSaved: Boolean
): Parcelable