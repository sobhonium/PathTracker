package com.pathtracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.pathtracker.data.database.PathDatabase
import com.pathtracker.data.entities.PathEntity

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = PathDatabase.getDatabase(application)
    val paths: LiveData<List<PathEntity>> = database.pathDao().getAllPaths()

}