package com.pathtracker.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "paths")
data class PathEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val startTime: Long,
    val endTime: Long?,
    val totalDistance: Double = 0.0,
    val averageSpeed: Double = 0.0,
    val rating: Float = 0f,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "path_points")
data class PathPointEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val pathId: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val timestamp: Long,
    val accuracy: Float = 0f
)

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val pathId: String,
    val latitude: Double,
    val longitude: Double,
    val filePath: String,
    val caption: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val pathId: String,
    val latitude: Double?,
    val longitude: Double?,
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)