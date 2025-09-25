package com.pathtracker.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.pathtracker.data.database.PathDatabase
import com.pathtracker.data.entities.CommentEntity
import com.pathtracker.data.entities.PathEntity
import com.pathtracker.databinding.ActivityRecordPathBinding
import com.pathtracker.services.LocationTrackingService
import com.pathtracker.utils.KmlExporter
import kotlinx.coroutines.launch
import java.util.*

class RecordPathActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordPathBinding
    private lateinit var database: PathDatabase
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentPath: PathEntity? = null
    private var startTime: Long = 0
    private var timer: Timer? = null
    private var isRecording = false

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordPathBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = PathDatabase.getDatabase(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkLocationPermission()) {
            startRecording()
        } else {
            requestLocationPermission()
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnCamera.setOnClickListener {
            currentPath?.let { path ->
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra(CameraActivity.EXTRA_PATH_ID, path.id)
                startActivity(intent)
            }
        }

        binding.btnAddComment.setOnClickListener {
            addComment()
        }

        binding.btnStopRecording.setOnClickListener {
            showStopRecordingDialog()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording()
            } else {
                Toast.makeText(this, "Location permission required for tracking", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startRecording() {
        if (isRecording) return

        isRecording = true
        startTime = System.currentTimeMillis()

        val pathName = if (binding.etPathName.text.toString().isBlank()) {
            "Path ${Date()}"
        } else {
            binding.etPathName.text.toString()
        }

        currentPath = PathEntity(
            name = pathName,
            description = "",
            startTime = startTime,
            endTime = null,
            isCompleted = false
        )

        lifecycleScope.launch {
            database.pathDao().insertPath(currentPath!!)
        }

        // Start location tracking service
        val serviceIntent = Intent(this, LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_START_TRACKING
            putExtra(LocationTrackingService.EXTRA_PATH_ID, currentPath!!.id)
        }
        ContextCompat.startForegroundService(this, serviceIntent)

        // Start timer
        startTimer()

        Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
    }

    private fun startTimer() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val elapsed = System.currentTimeMillis() - startTime
                runOnUiThread {
                    updateTimerDisplay(elapsed)
                }
            }
        }, 0, 1000)
    }

    private fun updateTimerDisplay(elapsedMillis: Long) {
        val seconds = (elapsedMillis / 1000) % 60
        val minutes = (elapsedMillis / (1000 * 60)) % 60
        val hours = (elapsedMillis / (1000 * 60 * 60)) % 24

        binding.tvTimer.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun addComment() {
        val commentText = binding.etComment.text.toString()
        if (commentText.isBlank()) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show()
            return
        }

        currentPath?.let { path ->
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    val comment = CommentEntity(
                        pathId = path.id,
                        latitude = location?.latitude,
                        longitude = location?.longitude,
                        comment = commentText
                    )

                    lifecycleScope.launch {
                        database.pathDao().insertComment(comment)
                        runOnUiThread {
                            binding.etComment.text.clear()
                            Toast.makeText(this@RecordPathActivity, "Comment added", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: SecurityException) {
                // Fallback without location
                val comment = CommentEntity(
                    pathId = path.id,
                    latitude = null,
                    longitude = null,
                    comment = commentText
                )

                lifecycleScope.launch {
                    database.pathDao().insertComment(comment)
                    runOnUiThread {
                        binding.etComment.text.clear()
                        Toast.makeText(this@RecordPathActivity, "Comment added", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showStopRecordingDialog() {
        AlertDialog.Builder(this)
            .setTitle("Stop Recording")
            .setMessage("Do you want to stop recording and save your path?")
            .setPositiveButton("Save & Stop") { _, _ ->
                stopRecording()
            }
            .setNegativeButton("Continue Recording", null)
            .setNeutralButton("Export KML") { _, _ ->
                exportToKml()
            }
            .show()
    }

    private fun stopRecording() {
        if (!isRecording) return

        isRecording = false
        timer?.cancel()

        // Stop location tracking service
        val serviceIntent = Intent(this, LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_STOP_TRACKING
        }
        startService(serviceIntent)

        // Update path with completion data
        currentPath?.let { path ->
            val updatedPath = path.copy(
                endTime = System.currentTimeMillis(),
                isCompleted = true,
                rating = binding.ratingBar.rating,
                description = binding.etComment.text.toString()
            )

            lifecycleScope.launch {
                database.pathDao().updatePath(updatedPath)
            }
        }

        Toast.makeText(this, "Recording stopped and saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun exportToKml() {
        currentPath?.let { path ->
            lifecycleScope.launch {
                val pathPoints = database.pathDao().getPathPoints(path.id)
                val photos = database.pathDao().getPhotosForPath(path.id)
                val comments = database.pathDao().getCommentsForPath(path.id)

                val file = KmlExporter.exportToKml(this@RecordPathActivity, path, pathPoints, photos, comments)

                runOnUiThread {
                    if (file != null) {
                        Toast.makeText(this@RecordPathActivity, "KML exported to Downloads/${file.name}", Toast.LENGTH_LONG).show()

                        // Share the KML file
                        shareKmlFile(file)
                    } else {
                        Toast.makeText(this@RecordPathActivity, "Failed to export KML", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun shareKmlFile(file: java.io.File) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, androidx.core.content.FileProvider.getUriForFile(
                this@RecordPathActivity,
                "${packageName}.fileprovider",
                file
            ))
            type = "application/vnd.google-earth.kml+xml"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Share your path"))
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}