package com.pathtracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pathtracker.data.entities.*

@Dao
interface PathDao {
    @Query("SELECT * FROM paths ORDER BY createdAt DESC")
    fun getAllPaths(): LiveData<List<PathEntity>>

    @Query("SELECT * FROM paths WHERE id = :pathId")
    suspend fun getPathById(pathId: String): PathEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPath(path: PathEntity)

    @Update
    suspend fun updatePath(path: PathEntity)

    @Delete
    suspend fun deletePath(path: PathEntity)

    @Query("SELECT * FROM path_points WHERE pathId = :pathId ORDER BY timestamp")
    suspend fun getPathPoints(pathId: String): List<PathPointEntity>

    @Insert
    suspend fun insertPathPoint(point: PathPointEntity)

    @Query("SELECT * FROM photos WHERE pathId = :pathId ORDER BY timestamp")
    suspend fun getPhotosForPath(pathId: String): List<PhotoEntity>

    @Insert
    suspend fun insertPhoto(photo: PhotoEntity)

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    @Query("SELECT * FROM comments WHERE pathId = :pathId ORDER BY timestamp")
    suspend fun getCommentsForPath(pathId: String): List<CommentEntity>

    @Insert
    suspend fun insertComment(comment: CommentEntity)

    @Delete
    suspend fun deleteComment(comment: CommentEntity)
}