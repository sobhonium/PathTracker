package com.pathtracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pathtracker.activities.RecordPathActivity
import com.pathtracker.adapters.PathsAdapter
import com.pathtracker.databinding.ActivityMainBinding
import com.pathtracker.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var pathsAdapter: PathsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        binding.fabStartRecording.setOnClickListener {
            val intent = Intent(this, RecordPathActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        pathsAdapter = PathsAdapter { path ->
            // Handle path item click - navigate to detail view
            // val intent = Intent(this, PathDetailActivity::class.java)
            // intent.putExtra(PathDetailActivity.EXTRA_PATH_ID, path.id)
            // startActivity(intent)
        }

        binding.recyclerViewPaths.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = pathsAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.paths.observe(this) { paths ->
            pathsAdapter.submitList(paths)
        }
    }
}