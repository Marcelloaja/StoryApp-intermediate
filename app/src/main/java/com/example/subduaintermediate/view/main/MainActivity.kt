package com.example.subduaintermediate.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.subduaintermediate.call.ResultCall
import com.example.subduaintermediate.databinding.ActivityMainBinding
import com.example.subduaintermediate.view.ViewModelFactory
import com.example.subduaintermediate.view.adaptor.LoadingStateAdapter
import com.example.subduaintermediate.view.adaptor.MainListAdaptor
import com.example.subduaintermediate.view.adaptor.StoryLoadStateAdapter
import com.example.subduaintermediate.view.maps.MapsActivity
import com.example.subduaintermediate.view.upload.UploadActivity
import com.example.subduaintermediate.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainListAdapter: MainListAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.show()
        viewModel.getSession().observe(this) { user ->
            Log.d("MainActivity", "User session: $user")
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                setupRecyclerView()
                getData()
                setupAction()
            }
        }

        binding.fabMaps.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        binding.fabTambah.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }

        binding.fabLogout.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.logout()
                    val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            alertDialog.show()
        }

    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvMain.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvMain.addItemDecoration(itemDecoration)

        mainListAdapter = MainListAdaptor()
        binding.rvMain.adapter = mainListAdapter
    }

    private fun setupAction() {
        lifecycleScope.launch {
            viewModel.stories.observe(this@MainActivity) {
                mainListAdapter.submitData(lifecycle, it)
            }
        }
    }

    private fun getData() {
        binding.rvMain.adapter = mainListAdapter.withLoadStateFooter(
            footer = StoryLoadStateAdapter {
                mainListAdapter.retry()
            }
        )

        lifecycleScope.launch {
            viewModel.stories.observe(this@MainActivity) { pagingData ->
                mainListAdapter.submitData(lifecycle, pagingData)
            }
        }
    }

}
