package com.bettafish.flarent.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface DiscussionCacheDao {
    @Query(
        """
        SELECT * FROM discussion_list_cache
        WHERE cacheKey = :cacheKey
        ORDER BY position ASC
        """
    )
    suspend fun getCachedDiscussions(cacheKey: String): List<DiscussionCacheEntity>

    @Query(
        """
        SELECT * FROM discussion_list_cache
        WHERE discussionId = :discussionId
        """
    )
    suspend fun getCachedDiscussionRows(discussionId: String): List<DiscussionCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<DiscussionCacheEntity>)

    @Update
    suspend fun updateAll(items: List<DiscussionCacheEntity>)

    @Query("DELETE FROM discussion_list_cache WHERE cacheKey = :cacheKey")
    suspend fun clearCache(cacheKey: String)

    @Transaction
    suspend fun replaceCache(cacheKey: String, items: List<DiscussionCacheEntity>) {
        clearCache(cacheKey)
        insertAll(items)
    }
}
