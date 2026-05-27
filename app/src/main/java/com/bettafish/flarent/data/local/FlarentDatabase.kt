package com.bettafish.flarent.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DiscussionCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FlarentDatabase : RoomDatabase() {
    abstract fun discussionCacheDao(): DiscussionCacheDao
}
