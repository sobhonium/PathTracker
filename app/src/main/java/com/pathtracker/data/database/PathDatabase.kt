package com.pathtracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pathtracker.data.dao.PathDao
import com.pathtracker.data.entities.*

@Database(
    entities = [PathEntity::class, PathPointEntity::class, PhotoEntity::class, CommentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PathDatabase : RoomDatabase() {
    abstract fun pathDao(): PathDao

    companion object {
        @Volatile
        private var INSTANCE: PathDatabase? = null

        fun getDatabase(context: Context): PathDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PathDatabase::class.java,
                    "path_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}