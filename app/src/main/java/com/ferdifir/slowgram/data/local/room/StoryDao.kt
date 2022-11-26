package com.ferdifir.slowgram.data.local.room

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.ferdifir.slowgram.data.local.entity.StoryEntity

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(vararg story: StoryEntity)

    @Query("SELECT * FROM story ORDER BY date DESC")
    fun getStories(): PagingSource<Int, StoryEntity>

    @Query("DELETE FROM story")
    fun deleteAll()
}